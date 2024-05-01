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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    private final MemberNoticeRepository memberNoticeRepository;

    // 공지사항 등록
    public ResponseEntity<?> postNotice(NoticeDTO.BaseNotice dto) {
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
    public ResponseEntity<?> getNoticeList(boolean state) {
        String memberId = JwtUtil.getMemberIdFromToken();
        List<NoticeEntity> noticeEntityList = noticeRepository.findAll();

        // 공지사항이 없는 경우
        if (noticeEntityList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<NoticeDTO.NoticeList> noticeLists = new ArrayList<>();

        for (NoticeEntity noticeEntity:noticeEntityList) {
            // 현재 시간과 공지사항의 생성 시간 차이 계산
            boolean isNewNotice = ChronoUnit.DAYS.between(noticeEntity.getCreatedTime(), LocalDateTime.now()) <= 5;

            if (isNewNotice) {
                // 사용자가 공지사항을 조회했는지 확인
                boolean hasRead = memberNoticeRepository.existsByMemberEntity_MemberIdAndNoticeEntity_NoticeId(memberId, noticeEntity.getNoticeId());
                if (hasRead) {
                    isNewNotice = false;  // 이미 읽었다면 isNewNotice를 false로 설정
                }
            }

            if (noticeEntity.isDeleted() == state) {
                NoticeDTO.NoticeList noticeList = NoticeDTO.NoticeList.builder()
                        .noticeId(noticeEntity.getNoticeId())
                        .writerGen(noticeEntity.getMemberEntity().getMemberGen())
                        .writer(noticeEntity.getMemberEntity().getMemberName())
                        .title(noticeEntity.getNoticeTitle())
                        .noticeHit(noticeEntity.getNoticeHit())
                        .isImportant(noticeEntity.isImportant())
                        .isNewNotice(isNewNotice)
                        .createdTime(noticeEntity.getCreatedTime())
                        .build();

                noticeLists.add(noticeList);
            }
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
                .createdTime(noticeEntity.getCreatedTime())
                .updatedTime(noticeEntity.getUpdatedTime())
                .build();

        return ResponseEntity.ok().body(notice);
    }

    // 공지사항 업데이트
    public ResponseEntity<?> updateNotice(Long noticeId, NoticeDTO.BaseNotice dto) {
        String memberId = JwtUtil.getMemberIdFromToken();
        NoticeEntity noticeEntity =  noticeRepository.findById(noticeId)
                .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND,"해당 공지가 없습니다."));

        // 공지사항의 작성자 ID와 현재 로그인한 사용자의 ID를 비교
        if (!noticeEntity.getMemberEntity().getMemberId().equals(memberId)) {
            // 사용자가 공지사항의 작성자가 아닐 경우, 수정을 허용하지 않음
            throw new AppException(ErrorCode.FORBIDDEN, "이 공지사항을 수정할 권한이 없습니다.");
        }

        noticeEntity.setNoticeTitle(dto.getTitle());
        noticeEntity.setNoticeContent(dto.getContent());
        noticeEntity.setImportant(dto.isIsImportant());
        noticeEntity.setUpdatedTime(LocalDateTime.now());
        noticeRepository.save(noticeEntity);

        return ResponseEntity.ok().build();
    }

    // 새로운 공지사항 존재 확인(비회원 전용)
    public ResponseEntity<?> checkNewNotice() {
        List<NoticeEntity> notices = noticeRepository.findAll();
        boolean hasNewNotices = notices.stream()
                .anyMatch(notice -> ChronoUnit.DAYS.between(notice.getCreatedTime(), LocalDateTime.now()) <= 5);

        Map<String, Boolean> response = new HashMap<>();
        response.put("hasNewNotices", hasNewNotices);

        return ResponseEntity.ok().body(response);
    }

    // 공지사항 복원
    public ResponseEntity<?> restoreNotice(Long noticeId) {
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "해당 noticeID가 없습니다."));

        noticeEntity.setDeleted(false);
        noticeRepository.save(noticeEntity);

        return ResponseEntity.ok().build();
    }

    // 공지사항 삭제
    public ResponseEntity<?> deleteNotice(Long noticeId) {
//        List<MemberNoticeEntity> memberNotices = memberNoticeRepository.findByNoticeEntity_NoticeId(noticeId);
//        memberNoticeRepository.deleteAll(memberNotices);

//        noticeRepository.deleteById(noticeId);
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 공지가 없습니다."));
        noticeEntity.setDeleted(true);
        noticeRepository.save(noticeEntity);
        return ResponseEntity.ok().build();
    }
}
