package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.CommentDTO;
import com.powersupply.PES.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 가져오기
    @GetMapping("/api/comment/{answerId}")
    public ResponseEntity<?> getComment(@PathVariable Long answerId) {
        return commentService.getComment(answerId);
    }

    // 댓글 달기
    @PostMapping("/api/comment/{answerId}")
    public ResponseEntity<?> createComment(@PathVariable Long answerId,
                                           @RequestBody CommentDTO.CreateComment dto) {
        return commentService.createComment(answerId, dto);
    }
}
