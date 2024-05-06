package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.ManageDTO;
import com.powersupply.PES.service.ManageService;
import com.powersupply.PES.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class ManageController {

    private final ManageService manageService;

    // 전체 멤버 리스트 불러오기
    @GetMapping("/memberlist")
    public ResponseEntity<List<ManageDTO.MemberList>> list() {
        List<ManageDTO.MemberList> memberList = manageService.list();

        return ResponseEntity.ok().body(memberList);
    }

    // 특정 멤버 detail 불러오기
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ManageDTO.MemberList> readDetail(@PathVariable String memberId) {
        return ResponseEntity.ok().body(manageService.readDetail(memberId));
    }

    // 멤버 삭제하기
    @DeleteMapping("/member/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable String memberId) {
        return manageService.deleteMember(memberId);
    }
}
