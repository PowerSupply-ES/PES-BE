package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.ProblemDTO;
import com.powersupply.PES.domain.entity.AnswerEntity;
import com.powersupply.PES.domain.entity.ProblemEntity;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.AnswerRepository;
import com.powersupply.PES.repository.ProblemRepository;
import com.powersupply.PES.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final AnswerRepository answerRepository;

    // 문제 가져오기
    public ResponseEntity<?> getProblemList() {

        String id = JwtUtil.getMemberIdFromToken();

        List<Object[]> results = problemRepository.findAllProblemsWithAnswers(id);
        List<ProblemDTO.ProblemResponse> problemResponseList = new ArrayList<>();

        // 문제 리스트 비어있는지 체크
        if(results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        for (Object[] result : results) {
            ProblemEntity problemEntity = (ProblemEntity) result[0];
            AnswerEntity answerEntity = (AnswerEntity) result[1]; // 이 값이 null일 수 있음

            Long answerId = null;
            String answerState = null;
            Integer answerScore = null;

            if (answerEntity != null) {
                answerId = answerEntity.getAnswerId();
                answerState = answerEntity.getAnswerState();
                answerScore = answerEntity.getFinalScore();
            }

            ProblemDTO.ProblemResponse problemResponse = ProblemDTO.ProblemResponse.builder()
                    .problemId(problemEntity.getProblemId())
                    .problemTitle(problemEntity.getProblemTitle())
                    .problemScore(problemEntity.getProblemScore())
                    .answerCount(problemRepository.countStudentsWhoSolvedProblemWithStatus(problemEntity,"신입생"))
                    .answerId(answerId)
                    .answerState(answerState)
                    .myScore(answerScore)
                    .build();
            problemResponseList.add(problemResponse);
        }
        return ResponseEntity.ok(problemResponseList);
    }

//    public List<ProblemDTO.ProblemResponse> getAnswer(String problemId, String email) {
//        if(answerRepository.findByMemberEmail)
//    }

    // 문제 보기
//    public ProblemDTO.ShowProblem getProblem(Long problemId) {
//        ProblemEntity selectedEntity = problemRepository.findById(problemId)
//                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "잘못된 페이지 호출"));
//
//        return ProblemDTO.ShowProblem.builder()
//                .problemId(selectedEntity.getProblemId())
//                .problemTitle(selectedEntity.getProblemTitle())
//                .problemContent(selectedEntity.getProblemContent())
//                .problemScore(selectedEntity.getProblemScore())
//                .build();
//    }

    // 풀이 폼 가져오기
//    public ProblemDTO.SolveForm getSolveForm(Long problemId, String memberStuNum) {
//        problemRepository.findById(problemId).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND,"해당 문제가 없습니다."));
//
//         AnswerEntity answerEntity = answerRepository.findByMemberEntity_MemberStuNumAndProblemEntity_ProblemId(memberStuNum, problemId)
//                 .orElse(null);
//
//        if (answerEntity == null) {
//            return null;
//        }
//
//        LocalDateTime updateTime = answerEntity.getUpdatedTime();
//         if(updateTime == null) {
//             updateTime = answerEntity.getCreatedTime();
//         }
//
//         return ProblemDTO.SolveForm.builder()
//                 .answerState(answerEntity.getAnswerState())
//                 .answerUrl(answerEntity.getAnswerUrl())
//                 .questionContentFst(answerEntity.getQuestionFst().getQuestionContent())
//                 .questionContentSec(answerEntity.getQuestionSec().getQuestionContent())
//                 .answerFst(answerEntity.getAnswerFst())
//                 .answerSec(answerEntity.getAnswerSec())
//                 .updateTime(updateTime)
//                 .build();
//    }

    // 특정 문제의 요약 칼럼
//    public ProblemDTO.ShowProblemSimple getProblemSimple(Long problemId) {
//        ProblemEntity problemEntity = problemRepository.findById(problemId)
//                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 문제가 없습니다."));
//        return ProblemDTO.ShowProblemSimple.builder()
//                .problemId(problemEntity.getProblemId())
//                .problemTitle(problemEntity.getProblemTitle())
//                .problemScore(problemEntity.getProblemScore())
//                .build();
//    }

}
