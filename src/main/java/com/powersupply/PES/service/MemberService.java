package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.MemberDTO;
import com.powersupply.PES.domain.entity.MemberEntity;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.MemberRepository;
import com.powersupply.PES.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    @Value("${jwt.secret}")
    private String secretKey;

    public void signUp(MemberDTO.MemberSignUpRequest dto) {

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
                .memberScore(0)
                .memberEmail(dto.getMemberEmail())
                .memberGitUrl(dto.getMemberGitUrl())
                .build();
        memberRepository.save(memberEntity);
    }

    //로그인
    public String signIn(MemberDTO.MemberSignInRequest dto) {
        String stuNum = dto.getMemberStuNum();
        String pw = dto.getMemberPw();
        Long expireTimeMs = 1000 * 60 * 60l;

        // Email 및 password 빈칸 체크
        if (stuNum.isBlank() || pw.isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "필수 입력 사항을 입력해 주세요.");
        }

        // Email 없는 경우
        MemberEntity selectedMember = memberRepository.findByMemberStuNum(stuNum)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND, "해당 학번은 등록되지 않았습니다."));

        // password 틀린 경우
        if(!encoder.matches(pw, selectedMember.getMemberPw())){
            throw new AppException(ErrorCode.INVALID_INPUT, "패스워드를 잘못 입력 했습니다.");
        }

        return JwtUtil.createToken(selectedMember.getMemberStuNum(), secretKey, expireTimeMs);
    }

    public MemberEntity findByMemberStuNum(String memberStuNum) {
        return memberRepository.findByMemberStuNum(memberStuNum)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND, "해당 학번은 등록되지 않았습니다."));
    }
}
