package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.ProblemDTO;
import com.powersupply.PES.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/problemlist")
    public ResponseEntity<List<ProblemDTO.ProblemResponse>> getProblem() {
        return ResponseEntity.ok().body(problemService.getProblem());
    }
}
