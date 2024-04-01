package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.NoticeDTO;
import com.powersupply.PES.domain.entity.MemberNoticeEntity;
import com.powersupply.PES.domain.entity.NoticeEntity;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.MemberNoticeRepository;
import com.powersupply.PES.repository.MemberRepository;
import com.powersupply.PES.repository.NoticeRepository;
import com.powersupply.PES.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    private final MemberNoticeRepository memberNoticeRepository;

    // 공지사항 등록
    public ResponseEntity<?> postNotice(NoticeDTO.CreateNotice dto) {
        String memberId = JwtUtil.getMemberIdFromToken();

        NoticeEntity noticeEntity = NoticeEntity.builder()
                .noticeTitle(dto.getTitle())
                .noticeContent(dto.getContent())
                .isImportant(dto.isIsImportant())
                .memberEntity(memberRepository.findById(memberId).get())
                .build();
        noticeRepository.save(noticeEntity);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 공지사항 리스트 가져오기
    public ResponseEntity<?> getNoticeList() {
        List<NoticeEntity> noticeEntityList = noticeRepository.findAll();

        if (noticeEntityList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<NoticeDTO.NoticeList> noticeLists = new ArrayList<>();

        for (NoticeEntity noticeEntity:noticeEntityList) {
            NoticeDTO.NoticeList noticeList = NoticeDTO.NoticeList.builder()
                    .noticeId(noticeEntity.getNoticeId())
                    .writerGen(noticeEntity.getMemberEntity().getMemberGen())
                    .writer(noticeEntity.getMemberEntity().getMemberName())
                    .title(noticeEntity.getNoticeTitle())
                    .noticeHit(noticeEntity.getNoticeHit())
                    .isImportant(noticeEntity.isImportant())
                    .build();
            noticeLists.add(noticeList);
        }

        return ResponseEntity.ok().body(noticeLists);
    }

    // 공지사항 내용 가져오기
    public ResponseEntity<?> getNotice(Long noticeId) {

        String memberId = JwtUtil.getMemberIdFromToken();

        // 공지 조회하기
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 공지가 없습니다."));

        /* 멤버 공지 테이블에 저장 로직
        1. DB에 저장되어 있는지 확인
        2. 저장되어 있지 않다면 저장
         */
        if(!"anonymousUser".equals(memberId)) {
            boolean alreadyRead = memberNoticeRepository.existsByMemberEntity_MemberIdAndNoticeEntity_NoticeId(memberId, noticeId);
            if (!alreadyRead) {
                MemberNoticeEntity memberNoticeEntity = MemberNoticeEntity.builder()
                        .memberEntity(memberRepository.findById(memberId).get())
                        .noticeEntity(noticeEntity)
                        .build();
                memberNoticeRepository.save(memberNoticeEntity);
            }
        }

        // 조회수 늘리고 저장
        noticeEntity.setNoticeHit(noticeEntity.getNoticeHit() + 1);
        noticeRepository.save(noticeEntity);

        // 반환하기 위한 DTO 생성
        NoticeDTO.Notice notice = NoticeDTO.Notice.builder()
                .title(noticeEntity.getNoticeTitle())
                .content(noticeEntity.getNoticeContent())
                .writerGen(noticeEntity.getMemberEntity().getMemberGen())
                .writer(noticeEntity.getMemberEntity().getMemberName())
                .noticeHit(noticeEntity.getNoticeHit())
                .isImportant(noticeEntity.isImportant())
                .build();

        return ResponseEntity.ok().body(notice);
    }
}
