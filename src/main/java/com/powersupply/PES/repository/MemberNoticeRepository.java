package com.powersupply.PES.repository;

import com.powersupply.PES.domain.entity.MemberNoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberNoticeRepository extends JpaRepository<MemberNoticeEntity, Long> {
    boolean existsByMemberEntity_MemberIdAndNoticeEntity_NoticeId(String memberId, Long noticeId);
}
