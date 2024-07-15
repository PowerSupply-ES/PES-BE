package com.powersupply.PES.answer.controller;

import com.powersupply.PES.answer.dto.AnswerDTO;
import com.powersupply.PES.answer.service.AnswerService;
import com.powersupply.PES.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Answer", description = "답변 관련 API")
public class AnswerController {

    private final AnswerService answerService;

    @Operation(summary = "답변 생성", description = "사용자가 답변을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "답변 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/api/answer")
    public ResponseEntity<AnswerDTO.GetAnswerId> createAnswer(
            @Parameter(description = "회원 이메일", example = "user@example.com") @RequestParam("memberEmail") String email,
            @Parameter(description = "문제 ID", example = "1") @RequestParam("problemId") Long problemId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(answerService.createAnswer(email, problemId));
    }

    @Operation(summary = "질문 및 답변 조회", description = "특정 답변 ID에 대한 질문과 답변을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공(토큰에 문제가 없고 유저를 DB에서 찾을 수 있는 경우)"),
            @ApiResponse(responseCode = "404", description = "해당 answerId를 찾을 수 없는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/answer/{answerId}")
    public ResponseEntity<AnswerDTO.GetAnswer> getAnswer(
            @Parameter(description = "답변 ID", example = "1") @PathVariable Long answerId) {
        return ResponseEntity.ok().body(answerService.getAnswer(answerId));
    }

    @Operation(summary = "답변하기", description = "사용자가 특정 답변 ID에 대해 답변을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 작성 성공"),
            @ApiResponse(responseCode = "400", description = "answer 내용이 null인 경우 or 이미 댓글이 있어 수정이 불가능 한 경우"),
            @ApiResponse(responseCode = "404", description = "answer의 주인과 토큰이 다른 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/api/answer/{answerId}")
    public ResponseEntity<?> postAnswer(
            @Parameter(description = "답변 ID", example = "1") @PathVariable Long answerId,
            @RequestBody AnswerDTO.AnswerContent dto) {
        answerService.postAnswer(answerId, dto);
        return ResponseUtil.successResponse("");
    }

    @Operation(summary = "풀이 보기", description = "특정 문제 ID에 대한 풀이 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "토큰에 문제가 없고 유저를 DB에서 찾을 수 있는 경우"),
            @ApiResponse(responseCode = "204", description = "아직 푼 사람이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "problemId가 잘못된 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/answerlist/{problemId}")
    public ResponseEntity<?> getAnswerList(
            @Parameter(description = "문제 ID", example = "1") @PathVariable Long problemId) {
        return answerService.getAnswerList(problemId);
    }
}