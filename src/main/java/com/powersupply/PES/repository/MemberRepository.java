package com.powersupply.PES.repository;

import com.powersupply.PES.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {

    Optional<MemberEntity> findByMemberEmail(String email);

    Optional<MemberEntity> findByMemberPhone(String phone);

    List<MemberEntity> findByMemberGen(Integer memberGen);

    @Query("Select m From MemberEntity m WHERE m.memberStatus = :status1 OR m.memberStatus = :status2")
    List<MemberEntity> findByMemberStatus(@Param("status1") String status1, @Param("status2") String status2);
}
