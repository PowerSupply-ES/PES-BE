package com.powersupply.PES.question.controller;

import com.powersupply.PES.question.dto.QuestionDTO;
import com.powersupply.PES.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "Question", description = "질문 관련 API")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "(재)질문 목록 보기", description = "특정 문제에 대한 (재)질문 목록을 가져옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 problemId가 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/questions/{problemId}")
    public ResponseEntity<List<QuestionDTO.QuestionList>> getQuestionList(@PathVariable Long problemId) {
        return ResponseEntity.ok().body(questionService.getQeustionList(problemId));
    }
}
