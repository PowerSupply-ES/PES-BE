package com.powersupply.PES.problem.controller;

import com.powersupply.PES.problem.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Problem", description = "문제 관련 API")
public class ProblemController {

    private final ProblemService problemService;

    @Operation(summary = "문제 리스트 가져오기", description = "전체 문제 리스트를 가져옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰에 문제가 없고 유저를 DB에서 찾을 수 있음\n" +
                    "- 로그인 되어있고, 해당 문제를 푼 경우 → answerId 및 answerState를 포함하여 전송\n" +
                    "- 나머지 경우 → answerId 및 answerState는 null로 전송\n" +
                    "- answerScore가 없은 경우는 null로 전송 → 타입도 Integer임"),
            @ApiResponse(responseCode = "204", description = "리스트가 비어있는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/problemlist")
    public ResponseEntity<?> getProblemList() {
        return problemService.getProblemList();
    }
}
