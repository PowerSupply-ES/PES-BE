package com.powersupply.PES.manage.service;

import com.powersupply.PES.answer.entity.AnswerEntity;
import com.powersupply.PES.answer.repository.AnswerRepository;
import com.powersupply.PES.comment.repository.CommentRepository;
import com.powersupply.PES.manage.dto.ManageDTO;
import com.powersupply.PES.member.dto.MemberDTO;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.mapper.MapperUtils;
import com.powersupply.PES.member.entity.MemberEntity;
import com.powersupply.PES.member.repository.MemberRepository;
import com.powersupply.PES.problem.entity.ProblemEntity;
import com.powersupply.PES.problem.repository.ProblemRepository;
import com.powersupply.PES.question.entity.QuestionEntity;
import com.powersupply.PES.question.repository.QuestionRepository;
import com.powersupply.PES.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageService {
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    /* ---------- 문제 관리 기능 관련 ---------- */

    // 전체 문제 리스트 불러오기
    public List<ManageDTO.ProblemList> problemList() {
        return problemRepository.findAll().stream()
                .map(ManageDTO.ProblemList::new)
                .collect(Collectors.toList());
    }

    // 특정 문제 detail 불러오기
    public ManageDTO.ProblemDetail problemDetail(Long problemId) {
        return problemRepository.findById(problemId)
                .map(ManageDTO.ProblemDetail::new)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 문제가 존재하지 않습니다."));
    }

    // 문제 등록하기
    public ResponseEntity<?> postProblem(ManageDTO.ProblemRequestDto requestDto) {
        String id = JwtUtil.getMemberIdFromToken();

        MemberEntity admin = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 memberId가 없음"));

        if (!admin.getMemberStatus().equals("관리자")) {
            throw new AppException(ErrorCode.FORBIDDEN,"관리자가 아님");
        } else {
            ProblemEntity problemEntity = ProblemEntity.builder()
                    .problemTitle(requestDto.getProblemTitle())
                    .problemScore(requestDto.getProblemScore())
                    .context(requestDto.getContext())
                    .sample(requestDto.getSample())
                    .inputs(requestDto.getInputs())
                    .outputs(requestDto.getOutputs())
                    .build();
            problemRepository.save(problemEntity);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    // 문제 수정하기
    public ResponseEntity<?> patchProblem(Long problemId, ManageDTO.ProblemRequestDto requestDto) {
        String id = JwtUtil.getMemberIdFromToken();

        MemberEntity admin = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 memberId가 없습니다."));

        ProblemEntity problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 problemId가 없습니다."));

        if (!admin.getMemberStatus().equals("관리자")) {
            throw new AppException(ErrorCode.FORBIDDEN,"관리자가 아님");
        } else {
            problem.setProblemTitle(requestDto.getProblemTitle());
            problem.setProblemScore(requestDto.getProblemScore());
            problem.setContext(requestDto.getContext());
            problem.setSample(requestDto.getSample());
            problem.setInputs(requestDto.getInputs());
            problem.setOutputs(requestDto.getOutputs());
            problemRepository.save(problem);

            return ResponseEntity.ok().build();
        }
    }

    /* ---------- 회원 관리 기능 관련 ---------- */

    // 전체 멤버 리스트 불러오기
    @Transactional(readOnly = true)
    public List<ManageDTO.MemberList> list() {
        return memberRepository.findAll().stream()
                .map(ManageDTO.MemberList::new)
                .collect(Collectors.toList());
    }

    // 특정 멤버 detail 불러오기
    @Transactional
    public ManageDTO.MemberDetail readDetail(String memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 memberId가 없습니다."));

        Hibernate.initialize(member.getAnswerEntities());
        Hibernate.initialize(member.getCommentEntities());

        List<MemberDTO.MemberMySolveResponse> mySolveResponseList = answerRepository.findAllByMemberEntity_MemberId(memberId).stream()
                .map(MapperUtils::toMemberMySolveResponse)
                .collect(Collectors.toList());

        List<MemberDTO.MemberMyFeedbackResponse> myFeedbackResponseList = commentRepository.findAllByMemberEntity_MemberId(memberId).stream()
                .map(MapperUtils::toMemberMyFeedbackResponse)
                .collect(Collectors.toList());

        return ManageDTO.MemberDetail.builder()
                .member(member)
                .mySolveResponse(mySolveResponseList)
                .myFeedbackResponse(myFeedbackResponseList)
                .build();
    }

    // 멤버 삭제하기
    public ResponseEntity<?> deleteMember(String memberId) {
        String id = JwtUtil.getMemberIdFromToken();

        MemberEntity admin = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 memberId가 없음"));

        memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"삭제하려는 memberId가 없음"));

        if (!admin.getMemberStatus().equals("관리자")) {
            throw new AppException(ErrorCode.FORBIDDEN,"관리자가 아님");
        } else {
            memberRepository.deleteById(memberId);

            return ResponseEntity.ok().build();
        }
    }

    public MemberEntity updateMemberStatus(String memberId, ManageDTO.MemberUpdateRequestDto updateRequestDto) {
        String id = JwtUtil.getMemberIdFromToken();
        assert id != null;

        MemberEntity admin = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 memberId가 없음"));

        Optional<MemberEntity> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND,"해당 memberId가 없음");
        }

        if (!admin.getMemberStatus().equals("관리자")) {
            throw new AppException(ErrorCode.FORBIDDEN,"관리자가 아님");
        } else {
            MemberEntity member = optionalMember.get();
            member.setMemberStatus(updateRequestDto.getMemberStatus());

            return memberRepository.save(member);
        }
    }

    /* ---------- 질문 관리 기능 관련 ---------- */

    // 문제 별 질문 목록 가져오기
    public List<ManageDTO.QuestionList> questionList(Long problemId) {
        return questionRepository.findByProblemEntity_ProblemId(problemId).stream()
                .map(ManageDTO.QuestionList::new)
                .collect(Collectors.toList());
    }

    // 질문 등록하기
    public ResponseEntity<?> postQuestion(Long problemId, ManageDTO.QuestionRequestDto requestDto) {
        String id = JwtUtil.getMemberIdFromToken();

        MemberEntity admin = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 memberId가 없음"));

        if (!admin.getMemberStatus().equals("관리자")) {
            throw new AppException(ErrorCode.FORBIDDEN, "관리자가 아님");
        } else {
            ProblemEntity problemEntity = problemRepository.findById(problemId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 problemId가 없음"));

            QuestionEntity questionEntity = QuestionEntity.builder()
                    .questionContent(requestDto.getQuestionContent())
                    .problemEntity(problemEntity)
                    .build();
            questionRepository.save(questionEntity);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    // 질문 수정하기
    public ResponseEntity<?> updateQuestion(Long questionId, ManageDTO.QuestionRequestDto requestDto) {
        String id = JwtUtil.getMemberIdFromToken();

        MemberEntity admin = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 memberId가 없음"));

        QuestionEntity questionEntity = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 questionId가 없음"));

        if (!admin.getMemberStatus().equals("관리자")) {
            throw new AppException(ErrorCode.FORBIDDEN, "관리자가 아님");
        } else {
            questionEntity.setQuestionContent(requestDto.getQuestionContent());
            questionEntity.setUpdatedTime(LocalDateTime.now());
            questionRepository.save(questionEntity);

            return ResponseEntity.ok().build();
        }
    }

    // 질문 삭제하기
    public ResponseEntity<?> deleteQuestion(Long questionId) {
        String id = JwtUtil.getMemberIdFromToken();

        MemberEntity admin = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 memberId가 없음"));

        QuestionEntity questionEntity = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 questionId가 없음"));

        if (!admin.getMemberStatus().equals("관리자")) {
            throw new AppException(ErrorCode.FORBIDDEN, "관리자가 아님");
        } else {
            List<AnswerEntity> relatedQuestion = answerRepository.findByQuestionFstOrQuestionSec(questionEntity, questionEntity);

            if (!relatedQuestion.isEmpty()) {
                throw new AppException(ErrorCode.FORBIDDEN, "이미 답변을 한 학생이 있어, 삭제가 불가능합니다.");
            } else {
                questionRepository.delete(questionEntity);
                return ResponseEntity.noContent().build();
            }
        }
    }
}