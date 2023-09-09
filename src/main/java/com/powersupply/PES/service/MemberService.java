package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.MemberDTO;
import com.powersupply.PES.domain.entity.MemberEntity;
import com.powersupply.PES.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void join(MemberDTO.MemberJoinRequest dto) {

        // 저장
        MemberEntity memberEntity = MemberEntity.builder()
                .memberStuNum(dto.getMemberStuNum())
                .memberPw(dto.getMemberPw())
                .memberName(dto.getMemberName())
                .memberCardiNum(dto.getMemberCardiNum())
                .memberMajor(dto.getMemberMajor())
                .memberPhone(dto.getMemberPhone())
                .memberStatus("신입생")
                .memberScore("0")
                .memberEmail(dto.getMemberEmail())
                .memberGitUrl(dto.getMemberGitUrl())
                .build();
        memberRepository.save(memberEntity);
    }
}
