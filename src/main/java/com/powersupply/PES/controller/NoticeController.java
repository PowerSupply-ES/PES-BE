package com.powersupply.PES.controller;

import com.powersupply.PES.domain.dto.NoticeDTO;
import com.powersupply.PES.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 등록
    @PostMapping("/api/notice")
    public ResponseEntity<?> postNotice(@RequestBody NoticeDTO.CreateNotice dto) {
        return noticeService.postNotice(dto);
    }

    // 공지사항 리스트 가져오기
    @GetMapping("/api/notice")
    public ResponseEntity<?> getNoticeList() {
        return noticeService.getNoticeList();
    }

    // 공지사항 내용 가져오기
    @GetMapping("/api/notice/{noticeId}")
    public ResponseEntity<?> getNotice(@PathVariable Long noticeId) {
        return noticeService.getNotice(noticeId);
    }
}
