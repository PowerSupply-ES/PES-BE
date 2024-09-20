package com.powersupply.PES.manage.controller;

import com.powersupply.PES.manage.dto.ManageDTO;
import com.powersupply.PES.manage.dto.ManageDTO.MemberResponseDto;
import com.powersupply.PES.member.entity.MemberEntity;
import com.powersupply.PES.manage.service.ManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class ManageController {

    private final ManageService manageService;

    /* ---------- 문제 관리 기능 ---------- */

    // 전체 문제 리스트 불러오기
    @GetMapping("/problemlist")
    public ResponseEntity<List<ManageDTO.ProblemList>> problemList() {
        List<ManageDTO.ProblemList> problemList = manageService.problemList();
        return ResponseEntity.ok().body(problemList);
    }

    // 특정 문제 detail 불러오기
    @GetMapping("/problem/{problemId}")
    public ResponseEntity<ManageDTO.ProblemDetail> problemDetail(@PathVariable Long problemId) {
        return ResponseEntity.ok().body(manageService.problemDetail(problemId));
    }

    // 문제 등록하기
    @PostMapping("/problem")
    public ResponseEntity<?> postProblem(@RequestBody ManageDTO.ProblemRequestDto requestDto) throws IOException {
        return ResponseEntity.ok(manageService.postProblem(requestDto));
    }

    // 문제 수정하기
    @PatchMapping("/problem/{problemId}")
    public ResponseEntity<?> patchProblem(@PathVariable Long problemId, @RequestBody ManageDTO.ProblemRequestDto requestDto) {
        return manageService.patchProblem(problemId, requestDto);
    }

    /* ---------- 회원 관리 기능 ---------- */

    // 전체 멤버 리스트 불러오기
    @GetMapping("/memberlist")
    public ResponseEntity<List<ManageDTO.MemberList>> list() {
        List<ManageDTO.MemberList> memberList = manageService.list();

        return ResponseEntity.ok().body(memberList);
    }

    // 특정 멤버 detail 불러오기
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ManageDTO.MemberDetail> readDetail(@PathVariable String memberId) {
        return ResponseEntity.ok().body(manageService.readDetail(memberId));
    }

    // 멤버 삭제하기
    @DeleteMapping("/member/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable String memberId) {
        return manageService.deleteMember(memberId);
    }

    // 멤버 상태 수정하기
    @PutMapping("/member/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMemberStatus(@PathVariable String memberId, @RequestBody ManageDTO.MemberUpdateRequestDto requestDto) {
        return ResponseEntity.ok(manageService.updateMemberStatus(memberId, requestDto));
    }

    /* ---------- 질문 관리 기능 ---------- */

    // 문제 별 질문 목록 가져오기
    @GetMapping("/questionlist/{problemId}")
    public ResponseEntity<List<ManageDTO.QuestionList>> questionList(@PathVariable Long problemId) {
        List<ManageDTO.QuestionList> questionList = manageService.questionList(problemId);

        return ResponseEntity.ok().body(questionList);
    }

    // 질문 등록하기
    @PostMapping("/question/{problemId}")
    public ResponseEntity<?> postQuestion(@PathVariable Long problemId, @RequestBody ManageDTO.QuestionRequestDto requestDto) {
        return ResponseEntity.ok(manageService.postQuestion(problemId, requestDto));
    }

    // 질문 수정하기
    @PatchMapping("/question/{questionId}")
    public ResponseEntity<?> patchQuestion(@PathVariable Long questionId, @RequestBody ManageDTO.QuestionRequestDto requestDto) {
        return ResponseEntity.ok(manageService.updateQuestion(questionId, requestDto));
    }

    // 질문 삭제하기
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(manageService.deleteQuestion(questionId));
    }
}
