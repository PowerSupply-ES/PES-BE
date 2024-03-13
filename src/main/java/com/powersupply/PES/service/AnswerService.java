package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.AnswerDTO;
import com.powersupply.PES.domain.entity.*;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.*;
import com.powersupply.PES.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final QuestionRepository questionRepository;
    private final CommentRepository commentRepository;

    // answer 만들기
    @Transactional
    public AnswerDTO.GetAnswerId createAnswer(String id, Long problemId) {

        MemberEntity memberEntity = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "멤버 정보를 찾을 수 없습니다."));

        // DB에 해당 id의 problemId의 answer이 있는지 확인
        if (answerRepository.findByMemberEntity_MemberIdAndProblemEntity_ProblemId(id, problemId).isPresent()) {
            throw new AppException(ErrorCode.BAD_REQUEST,"해당 내용은 이미 있습니다.");
        }

        // 무작위 2개의 질문 선택
        List<QuestionEntity> questions = questionRepository.findByProblemEntity_ProblemId(problemId);

        // 질문이 2개보다 적은 경우
        if (questions.size() < 2) {
            log.error("문제에 대한 충분한 질문이 없습니다.");
            throw new AppException(ErrorCode.INVALID_INPUT,"문제에 대한 충분한 질문이 없습니다.");
        }
        Collections.shuffle(questions);
        List<QuestionEntity> selectedQuestions = questions.subList(0, 2);

        // 문제 연결을 위한 Entity 찾기
        Optional<ProblemEntity> problemEntityOptional = problemRepository.findById(problemId);
        if (problemEntityOptional.isEmpty()) {
            log.error("문제 정보를 찾을 수 없습니다.");
            throw new AppException(ErrorCode.NOT_FOUND, "문제 정보를 찾을 수 없습니다.");
        }
        ProblemEntity problemEntity = problemEntityOptional.get();

        // DB에 없을 경우 answer 생성 후 뮨제 상태 Grading으로 수정
        AnswerEntity answerEntity = AnswerEntity.builder()
                .memberEntity(memberEntity)
                .problemEntity(problemEntity)
                .answerState("test")
                .questionFst(selectedQuestions.get(0))
                .questionSec(selectedQuestions.get(1))
                .build();
        Long answerId = answerRepository.save(answerEntity).getAnswerId();

        return AnswerDTO.GetAnswerId.builder()
                .answerId(answerId)
                .build();
    }

    // 질문 답변 가져오기
    public AnswerDTO.GetAnswer getAnswer(Long answerId) {
        AnswerEntity answerEntity = answerRepository.findById(answerId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 answerId가 없음"));

        return AnswerDTO.GetAnswer.builder()
                .questionContentFst(answerEntity.getQuestionFst().getQuestionContent())
                .questionContentSec(answerEntity.getQuestionSec().getQuestionContent())
                .answerFst(answerEntity.getAnswerFst())
                .answerSec(answerEntity.getAnswerSec())
                .answerState(answerEntity.getAnswerState())
                .build();
    }

    // 답변 하기
    public void postAnswer(Long answerId, AnswerDTO.AnswerContent dto) {
        String id = JwtUtil.getMemberIdFromToken();

        AnswerEntity answerEntity = answerRepository.findById(answerId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 answerId가 없음"));

        if(!id.equals(answerEntity.getMemberEntity().getMemberId())) {
            throw new AppException(ErrorCode.FORBIDDEN,"아이디가 다름");
        }

        if (dto.getAnswerFst() == null || dto.getAnswerFst().isEmpty() ||
                dto.getAnswerSec() == null || dto.getAnswerSec().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "답변 내용 중 하나 또는 둘 다 비어 있음");
        }

        Optional<List<CommentEntity>> commentEntitiesOptional = commentRepository.findByAnswerEntity_AnswerId(answerId);

        // 댓글이 있는 경우
        if (commentEntitiesOptional.isPresent() && !commentEntitiesOptional.get().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "이미 댓글이 있어 수정 불가능");
        }

        answerEntity.setAnswerFst(dto.getAnswerFst());
        answerEntity.setAnswerSec(dto.getAnswerSec());
        answerEntity.setAnswerState("comment");

        answerRepository.save(answerEntity);
    }

    // answerList 가져오기
    @Transactional
    public ResponseEntity<?> getAnswerList(Long problemId) {
        // problemId 조회
        ProblemEntity problemEntity = problemRepository.findById(problemId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND,"problemId가 잘못 됨"));

        // answerEntity 가져오기
        List<AnswerEntity> answerEntityList= answerRepository.findAllByProblemEntity_ProblemId(problemId);

        if(answerEntityList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<AnswerDTO.GetAnswerList> answerLists = new ArrayList<>();

        for(AnswerEntity answerEntity: answerEntityList) {
            String answerState = answerEntity.getAnswerState();
            if (answerState.equals("question")) {
                continue;
            }
            AnswerDTO.GetAnswerList answerList = AnswerDTO.GetAnswerList.builder()
                    .answerId(answerEntity.getAnswerId())
                    .memberGen(answerEntity.getMemberEntity().getMemberGen())
                    .memberName(answerEntity.getMemberEntity().getMemberName())
                    .commentCount(answerEntity.getCommentEntities().size())
                    .answerState(answerState)
                    .build();
            answerLists.add(answerList);
        }
        return ResponseEntity.ok(answerLists);
    }
}
