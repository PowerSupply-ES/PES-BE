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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    // 공지사항 등록
    public ResponseEntity<?> postNotice(NoticeDTO.CreateNotice dto) {
        String memberId = JwtUtil.getMemberIdFromToken();

        System.out.println("isImportant: " + dto.isImportant());


        NoticeEntity noticeEntity = NoticeEntity.builder()
                .noticeTitle(dto.getTitle())
                .noticeContent(dto.getContent())
                .isImportant(dto.isImportant())
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
}
