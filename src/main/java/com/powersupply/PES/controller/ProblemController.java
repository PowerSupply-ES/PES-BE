package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.ProblemDTO;
import com.powersupply.PES.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProblemController {
/*
    private final ProblemService problemService;

    // 문제 리스트 가져오기
    @GetMapping("/api/problemlist")
    public ResponseEntity<List<ProblemDTO.ProblemResponse>> getProblemList() {
        return ResponseEntity.ok().body(problemService.getProblemList());
    }

    // 특정 문제의 요약 칼럼
    @GetMapping("/api/problem/{problemId}/simple")
    public ResponseEntity<ProblemDTO.ShowProblemSimple> getProblemSimple(@PathVariable Long problemId) {
        return ResponseEntity.ok().body(problemService.getProblemSimple(problemId));
    }

    // 문제 보기
    @GetMapping("/api/problem/{problemId}")
    public ResponseEntity<ProblemDTO.ShowProblem> getProblem(@PathVariable Long problemId) {
        return ResponseEntity.ok().body(problemService.getProblem(problemId));
    }

    // 풀이 form 보기
    @GetMapping("/api/problem/{problemId}/{memberStuNum}")
    public ResponseEntity<ProblemDTO.SolveForm> getSolveForm(@PathVariable Long problemId, @PathVariable String memberStuNum) {
        ProblemDTO.SolveForm solveForm = problemService.getSolveForm(problemId,memberStuNum);
        if(solveForm == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 상태 코드 반환
        }

        return ResponseEntity.ok().body(solveForm);
    }

 */
}
