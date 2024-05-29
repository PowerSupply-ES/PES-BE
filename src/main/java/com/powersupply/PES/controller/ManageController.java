package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.ManageDTO;
import com.powersupply.PES.domain.entity.MemberEntity;
import com.powersupply.PES.service.ManageService;
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
    public ResponseEntity<?> postProblem(@RequestBody ManageDTO.ProblemPostRequestDto requestDto) throws IOException {
        return ResponseEntity.ok(manageService.postProblem(requestDto));
    }

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
    public ResponseEntity<MemberEntity> updateMemberStatus(@PathVariable String memberId, @RequestBody ManageDTO.MemberUpdateRequestDto requestDto) {
        return ResponseEntity.ok(manageService.updateMemberStatus(memberId, requestDto));
    }
}
