package com.powersupply.PES.comment.controller;

import com.powersupply.PES.comment.dto.CommentDTO;
import com.powersupply.PES.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment", description = "댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 조회", description = "특정 답변 ID에 대한 댓글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "204", description = "댓글이 없는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/comment/{answerId}")
    public ResponseEntity<?> getComment(
            @Parameter(description = "답변 ID", example = "1") @PathVariable Long answerId) {
        return commentService.getComment(answerId);
    }

    @Operation(summary = "댓글 달기", description = "특정 답변 ID에 대해 댓글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "토큰에 문제가 없고 유저를 DB에서 찾아 정상적으로 댓글을 생성한 경우"),
            @ApiResponse(responseCode = "400", description = "이미 자신의 댓글이 있는 경우"),
            @ApiResponse(responseCode = "403", description = "재학생이 아닌 경우 / jwt 문제 / 자신의 답변에 댓글을 단 경우 / 최대 댓글 수 도달"),
            @ApiResponse(responseCode = "404", description = "answerId가 잘못된 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/api/comment/{answerId}")
    public ResponseEntity<?> createComment(
            @Parameter(description = "답변 ID", example = "1") @PathVariable Long answerId,
            @RequestBody CommentDTO.CreateComment dto) {
        return commentService.createComment(answerId, dto);
    }
}
