package com.powersupply.PES.notice.repository;

import com.powersupply.PES.notice.entity.MemberNoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberNoticeRepository extends JpaRepository<MemberNoticeEntity, Long> {
    boolean existsByMemberEntity_MemberIdAndNoticeEntity_NoticeId(String memberId, Long noticeId);

    List<MemberNoticeEntity> findByNoticeEntity_NoticeId(Long noticeId);
}
