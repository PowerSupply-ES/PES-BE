package com.powersupply.PES.answer.controller;

import com.powersupply.PES.answer.dto.AnswerDTO;
import com.powersupply.PES.answer.service.AnswerService;
import com.powersupply.PES.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    // answerEntity 만들기
    @PostMapping("/api/answer")
    public ResponseEntity<AnswerDTO.GetAnswerId> createAnswer(@RequestParam("memberEmail") String email, @RequestParam("problemId") Long problemId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(answerService.createAnswer(email, problemId));
    }

    // 질문, 답변 가져오기
    @GetMapping("/api/answer/{answerId}")
    public ResponseEntity<AnswerDTO.GetAnswer> getAnswer(@PathVariable Long answerId) {
        return ResponseEntity.ok().body(answerService.getAnswer(answerId));
    }

    // 답변하기
    @PostMapping("/api/answer/{answerId}")
    public ResponseEntity<?> postAnswer(@PathVariable Long answerId,
                                        @RequestBody AnswerDTO.AnswerContent dto) {
        answerService.postAnswer(answerId, dto);
        return ResponseUtil.successResponse("");
    }

    // 풀이 보기
    @GetMapping("/api/answerlist/{problemId}")
    public ResponseEntity<?> getAnswerList(@PathVariable Long problemId) {
        return answerService.getAnswerList(problemId);
    }
}
