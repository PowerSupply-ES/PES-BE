package com.powersupply.PES.comment.service;

import com.powersupply.PES.comment.dto.CommentDTO;
import com.powersupply.PES.answer.entity.AnswerEntity;
import com.powersupply.PES.comment.entity.CommentEntity;
import com.powersupply.PES.member.entity.MemberEntity;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.answer.repository.AnswerRepository;
import com.powersupply.PES.comment.repository.CommentRepository;
import com.powersupply.PES.member.repository.MemberRepository;
import com.powersupply.PES.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;

    // 댓글 가져오기
    @Transactional
    public ResponseEntity<?> getComment(Long answerId) {

        // answerId로 AnswerEntity 존재 여부 확인
        boolean isAnswerPresent = answerRepository.existsById(answerId);
        if (!isAnswerPresent) {
            // answerId가 존재하지 않는 경우
            throw new AppException(ErrorCode.NOT_FOUND,"");
        }

        Optional<List<CommentEntity>> commentEntitiesOptional = commentRepository.findByAnswerEntity_AnswerId(answerId);

        // 댓글 리스트가 비어있는 경우 204 No Content 반환
        if (!commentEntitiesOptional.isPresent() || commentEntitiesOptional.get().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<CommentEntity> commentEntities = commentEntitiesOptional.get();
        List<CommentDTO.GetComment> getCommentList = new ArrayList<>();

        for(CommentEntity commentEntity: commentEntities) {
            CommentDTO.GetComment getComment = CommentDTO.GetComment.builder()
                    .writerName(commentEntity.getMemberEntity().getMemberName())
                    .writerId(commentEntity.getMemberEntity().getMemberId())
                    .writerGen(commentEntity.getMemberEntity().getMemberGen())
                    .commentContent(commentEntity.getCommentContent())
                    .commentPassFail(commentEntity.getCommentPassFail())
                    .build();
            getCommentList.add(getComment);
        }
        return ResponseEntity.ok(getCommentList);
    }

    // 댓글 달기
    @Transactional
    public ResponseEntity<?> createComment(Long answerId, CommentDTO.CreateComment dto) {
        String id = JwtUtil.getMemberIdFromToken();

        // member 조회
        MemberEntity memberEntity = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN,"해당 아이디가 없다."));

        // answerEntity 불러오기 불러오기 실패 시 에러
        AnswerEntity answerEntity = answerRepository.findById(answerId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 answerId가 없습니다."));

        // answer의 id과 id 비교해서 같은 경우 에러
        if(answerEntity.getMemberEntity().getMemberId().equals(id)) {
            throw new AppException(ErrorCode.FORBIDDEN,"자신의 답변에는 댓글을 달 수 없습니다.");
        }

        // 댓글 리스트 조회
        List<CommentEntity> commentEntities = commentRepository.findByAnswerEntity_AnswerId(answerId)
                .orElse(new ArrayList<>());

        // 댓글 리스트가 2개 이상인 경우 에러 처리
        if (commentEntities.size() >= 2) {
            throw new AppException(ErrorCode.FORBIDDEN, "이미 최대 댓글 수에 도달했습니다.");
        }

        // Comment 생성
        CommentEntity newComment = CommentEntity.builder()
                .commentContent(dto.getComment())
                .commentPassFail(dto.getCommentPassFail())
                .memberEntity(memberEntity) // 또는 다른 멤버 엔티티를 참조해야 할 수도 있습니다
                .answerEntity(answerEntity)
                .build();

        // 댓글 리스트가 1개인 경우 -> pass/fail 지정
        if (commentEntities.size() == 1) {
            CommentEntity existingComment = commentEntities.get(0);

            if (id.equals(existingComment.getMemberEntity().getMemberId())) {
                throw new AppException(ErrorCode.BAD_REQUEST, "이미 해당 아이디의 댓글이 있습니다.");
            }

            int existingCommentPassFail = existingComment.getCommentPassFail();
            int newCommentPassFail = newComment.getCommentPassFail();
            int score = answerEntity.getProblemEntity().getProblemScore();

            if (existingCommentPassFail == 0 && newCommentPassFail == 0) {
                // 둘 다 fail일 경우
                score *= 0.4;
                answerEntity.setAnswerState("fail");
            } else if (existingCommentPassFail == 1 && newCommentPassFail == 1){
                // 둘 다 pass (1) 인 경우
                answerEntity.setAnswerState("success");
            } else {
                // 둘 중 1개가 pass (1) 인 경우
                score *= 0.7;
                answerEntity.setAnswerState("success");
            }
            answerEntity.setFinalScore(score);
            answerRepository.save(answerEntity);
        }
        commentRepository.save(newComment);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
