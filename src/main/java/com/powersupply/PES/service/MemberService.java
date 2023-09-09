package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.MemberDTO;
import com.powersupply.PES.domain.entity.MemberEntity;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    public void join(MemberDTO.MemberJoinRequest dto) {

        String StuNum = dto.getMemberStuNum();
        String pw = dto.getMemberPw();

        // Email 및 password 빈칸 체크
        if (StuNum.isBlank() || pw.isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "필수 입력 사항을 입력해 주세요.");
        }

        // memberEmail 중복 체크
        memberRepository.findByMemberStuNum(StuNum)
                .ifPresent(member -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, "해당 학번은 이미 있습니다.");
                });

        // 저장
        MemberEntity memberEntity = MemberEntity.builder()
                .memberStuNum(StuNum)
                .memberPw(encoder.encode(pw))
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
