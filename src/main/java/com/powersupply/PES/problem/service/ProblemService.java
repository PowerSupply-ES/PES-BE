package com.powersupply.PES.problem.service;

import com.powersupply.PES.problem.dto.ProblemDTO;
import com.powersupply.PES.answer.entity.AnswerEntity;
import com.powersupply.PES.problem.entity.ProblemEntity;
import com.powersupply.PES.problem.repository.ProblemRepository;
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
}
