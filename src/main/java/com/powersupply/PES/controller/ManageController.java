package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.ManageDTO;
import com.powersupply.PES.service.ManageService;
import com.powersupply.PES.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ManageController {

    private final ManageService manageService;

    @GetMapping("/management")
    public String getManagePage() {
        return "management";
    }

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
}
