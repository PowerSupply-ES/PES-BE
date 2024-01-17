package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.ManageDTO;
import com.powersupply.PES.service.ManageService;
import com.powersupply.PES.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ManageController {
/*
    private final ManageService manageService;

    // 질문 만들기
    @PostMapping("/api/manage/questions/{problemId}")
    public ResponseEntity<?> makeQuestion(@PathVariable Long problemId, ManageDTO.makeQuestion dto) {
        manageService.makeQuestion(problemId, dto);
        return ResponseUtil.successResponse("성공적으로 질문을 생성하였습니다.");
    }

    // 멤버 관리하기
    @GetMapping("/api/manage")
    public ResponseEntity<List<ManageDTO.MemberList>> getMemberList() {
        return ResponseEntity.ok().body(manageService.getMemberList());
    }

    // 멤버 수정하기
    @PatchMapping("/api/manage/{memberStuNum}")
    public ResponseEntity<?> patchMember(@PathVariable String memberStuNum, @RequestBody ManageDTO.PatchMember dto) {
        manageService.patchMember(memberStuNum, dto);
        return ResponseUtil.successResponse("성공적으로 수정하였습니다.");
    }

 */
}
