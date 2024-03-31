package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.NoticeDTO;
import com.powersupply.PES.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 등록
    @PostMapping("/api/notice")
    public ResponseEntity<?> postNotice(@RequestBody NoticeDTO.createNotice dto) {
        return noticeService.postNotice(dto);
    }
}
