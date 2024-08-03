package com.powersupply.PES.problem.controller;

import com.powersupply.PES.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    // 문제 리스트 가져오기
    @GetMapping("/api/problemlist")
    public ResponseEntity<?> getProblemList() {
        return problemService.getProblemList();
    }
}
