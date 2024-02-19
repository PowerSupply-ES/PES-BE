package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.CommentDTO;
import com.powersupply.PES.domain.entity.AnswerEntity;
import com.powersupply.PES.domain.entity.CommentEntity;
import com.powersupply.PES.domain.entity.MemberEntity;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.AnswerRepository;
import com.powersupply.PES.repository.CommentRepository;
import com.powersupply.PES.repository.MemberRepository;
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
                    .writerEmail(commentEntity.getMemberEntity().getMemberEmail())
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
        String email = JwtUtil.getMemberEmailFromToken();

        // member 조회
        MemberEntity memberEntity = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN,"해당 email가 없다."));

        // answerEntity 불러오기 불러오기 실패 시 에러
        AnswerEntity answerEntity = answerRepository.findById(answerId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 answerId가 없습니다."));

        // answer의 email과 email 비교해서 같은 경우 에러
        if(answerEntity.getMemberEntity().getMemberEmail().equals(email)) {
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

            if (email.equals(existingComment.getMemberEntity().getMemberEmail())) {
                throw new AppException(ErrorCode.BAD_REQUEST, "이미 해당 email의 댓글이 있습니다.");
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
                answerEntity.setAnswerState("pass");
            } else {
                // 둘 중 1개가 pass (1) 인 경우
                score *= 0.7;
                answerEntity.setAnswerState("pass");
            }
            answerEntity.setFinalScore(score);
            answerRepository.save(answerEntity);
        }
        commentRepository.save(newComment);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
/*

    // 댓글 보기
    @Transactional
    public List<CommentDTO.ViewComment> getViewComment(Long problemId, String memberStuNum) {
        AnswerEntity answerEntity = answerRepository.findByMemberEntity_MemberStuNumAndProblemEntity_ProblemId(memberStuNum,problemId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 답안이 없습니다."));

        List<CommentEntity> commentEntities = commentRepository.findByAnswerEntity(answerEntity);

        List<CommentDTO.ViewComment> viewComments = new ArrayList<>();

        for(CommentEntity commentEntity: commentEntities) {
            LocalDateTime updateTime = commentEntity.getUpdatedTime();
            if(updateTime == null) {
                updateTime = commentEntity.getCreatedTime();
            }
            CommentDTO.ViewComment viewComment = CommentDTO.ViewComment.builder()
                    .memberName(commentEntity.getMemberEntity().getMemberName())
                    .memberGen(commentEntity.getMemberEntity().getMemberGen())
                    .commentPassFail(commentEntity.getCommentPassFail())
                    .commentContent(commentEntity.getCommentContent())
                    .updateTime(updateTime)
                    .build();
            viewComments.add(viewComment);
        }

        return viewComments;
    }

    // 댓글 달기
    @Transactional
    public void saveComment(Long problemId, String memberStuNum, CommentDTO.PostComment dto) {

        // 본인 확인 로직
        if(!dto.getWriter().equals(JwtUtil.getMemberStuNumFromToken())){
            throw new AppException(ErrorCode.INVALID_INPUT,"확인할 수 없는 유저입니다.");
        }

        AnswerEntity answerEntity = answerRepository.findByMemberEntity_MemberStuNumAndProblemEntity_ProblemId(memberStuNum, problemId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 답안이 없습니다."));

        // 연결된 comment 가져오기
        List<CommentEntity> commentEntities = commentRepository.findByAnswerEntity(answerEntity);

        long ownCommentsCount = commentEntities.stream()
                .filter(comment -> comment.getMemberEntity().getMemberStuNum().equals(dto.getWriter()))
                .count();

        // comment에 자신이 등록한 커멘트가 있다면 오류 메시지 전송
        if (ownCommentsCount > 0) {
            throw new AppException(ErrorCode.INVALID_INPUT,"이미 자신이 등록한 커멘트가 있습니다.");
        }

        // comment가 3개라면 오류 메시지 전송
        if (commentEntities.size() >= 3) {
            throw new AppException(ErrorCode.INVALID_INPUT,"3개의 답변이 이미 존재합니다.");
        }

        // 오류가 없다면 저장
        CommentEntity commentEntity = CommentEntity.builder()
                .memberEntity(memberRepository.findByMemberStuNum(dto.getWriter()).get())
                .answerEntity(answerEntity)
                .commentPassFail(dto.getCommentPassFail())
                .commentContent(dto.getCommentContent())
                .build();
        commentRepository.save(commentEntity);

        commentEntities.add(commentEntity);

        // Pass와 Failure 개수 확인
        long passCount = commentEntities.stream()
                .filter(comment -> comment.getCommentPassFail() == 1)
                .count();
        long failCount = commentEntities.stream()
                .filter(comment -> comment.getCommentPassFail() == 0)
                .count();
        System.out.println("pass : " + passCount);

        // 조건에 따라 answerState 업데이트
        if (passCount >= 2) {
            answerEntity.setAnswerState("Success");
        } else if (failCount >= 2) {
            answerEntity.setAnswerState("Failure");
        }
        answerRepository.save(answerEntity);  // answerState 변경 사항 저장
    }

    // 답안 수정하기
    public void patchComment(Long problemId, String memberStuNum, CommentDTO.PatchComment dto) {
        // 본인 확인 로직
        if(!dto.getWriter().equals(JwtUtil.getMemberStuNumFromToken())){
            throw new AppException(ErrorCode.INVALID_INPUT,"확인할 수 없는 유저입니다.");
        }

        AnswerEntity answerEntity = answerRepository.findByMemberEntity_MemberStuNumAndProblemEntity_ProblemId(memberStuNum, problemId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 답안이 없습니다."));

        // 연결된 comment 가져오기
        CommentEntity commentEntity = commentRepository.findByAnswerEntityAndMemberEntity_MemberStuNum(answerEntity, dto.getWriter())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 댓글이 없습니다."));

        // 오류가 없다면 저장
        commentEntity.setCommentContent(dto.getCommentContent());

        commentRepository.save(commentEntity);
    }

    // (재)내가 쓴 댓글보기
    @Transactional
    public List<CommentDTO.MyComment> getMyComment() {
        String memberStuNum = JwtUtil.getMemberStuNumFromToken();
        List<CommentEntity> commentEntityList = commentRepository.findByMemberEntity_MemberStuNum(memberStuNum);
        List<CommentDTO.MyComment> myCommentList = new ArrayList<>();

        for(CommentEntity commentEntity: commentEntityList) {
            CommentDTO.MyComment myComment = CommentDTO.MyComment.builder()
                    .problemId(commentEntity.getAnswerEntity().getProblemEntity().getProblemId())
                    .memberStuNum(commentEntity.getAnswerEntity().getMemberEntity().getMemberStuNum())
                    .memberName(commentEntity.getAnswerEntity().getMemberEntity().getMemberName())
                    .commentContent(commentEntity.getCommentContent())
                    .commentPassFail(commentEntity.getCommentPassFail())
                    .build();
            myCommentList.add(myComment);
        }
        return myCommentList;
    }
    */
}
