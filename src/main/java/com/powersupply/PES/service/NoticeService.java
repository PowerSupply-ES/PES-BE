package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.NoticeDTO;
import com.powersupply.PES.domain.entity.NoticeEntity;
import com.powersupply.PES.repository.MemberRepository;
import com.powersupply.PES.repository.NoticeRepository;
import com.powersupply.PES.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    // 공지사항 등록
    public ResponseEntity<?> postNotice(NoticeDTO.createNotice dto) {
        String memberId = JwtUtil.getMemberIdFromToken();

        NoticeEntity noticeEntity = NoticeEntity.builder()
                .noticeTitle(dto.getTitle())
                .noticeContent(dto.getContent())
                .isImportant(dto.isImportant())
                .memberEntity(memberRepository.findById(memberId).get())
                .build();
        noticeRepository.save(noticeEntity);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}