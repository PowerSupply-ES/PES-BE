package com.powersupply.PES.repository;

import com.powersupply.PES.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {

    Optional<MemberEntity> findByMemberEmail(String email);

    Optional<MemberEntity> findByMemberPhone(String phone);

    List<MemberEntity> findByMemberGen(Integer memberGen);
}
