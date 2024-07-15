package com.powersupply.PES.notice.controller;

import com.powersupply.PES.notice.dto.NoticeDTO;
import com.powersupply.PES.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notice", description = "공지사항 관련 API")
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "새로운 공지사항 존재 확인", description = "비회원 전용 API로 새로운 공지사항이 있는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 통신 완료"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/notice/new")
    public ResponseEntity<?> checkNewNotice() {
        return noticeService.checkNewNotice();
    }

    @Operation(summary = "공지사항 등록", description = "새로운 공지사항을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 등록 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한이 없는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/api/notice")
    public ResponseEntity<?> postNotice(@RequestBody NoticeDTO.BaseNotice dto) {
        return noticeService.postNotice(dto);
    }

    @Operation(summary = "공지사항 리스트 가져오기", description = "공지사항 리스트를 가져옵니다. state가 true이면 삭제된 공지사항을 포함합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "204", description = "공지사항이 없는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/notice")
    public ResponseEntity<?> getNoticeList(@RequestParam(required = false) boolean state) {
        return noticeService.getNoticeList(state);
    }

    @Operation(summary = "공지사항 내용 가져오기", description = "특정 공지사항의 내용을 가져옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 noticeId가 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/notice/{noticeId}")
    public ResponseEntity<?> getNotice(@PathVariable Long noticeId) {
        return noticeService.getNotice(noticeId);
    }

    @Operation(summary = "공지사항 수정", description = "특정 공지사항의 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "해당 noticeId가 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/api/notice/{noticeId}")
    public ResponseEntity<?> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeDTO.BaseNotice dto) {
        return noticeService.updateNotice(noticeId, dto);
    }

    @Operation(summary = "공지사항 복구", description = "삭제된 공지사항을 복구합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "복구 성공"),
            @ApiResponse(responseCode = "404", description = "해당 noticeId가 없을 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/api/notice/restore/{noticeId}")
    public ResponseEntity<?> restoreNotice(@PathVariable Long noticeId) {
        return noticeService.restoreNotice(noticeId);
    }

    @Operation(summary = "공지사항 삭제", description = "특정 공지사항을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한이 없는 경우"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/api/notice/{noticeId}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long noticeId) {
        return noticeService.deleteNotice(noticeId);
    }
}
