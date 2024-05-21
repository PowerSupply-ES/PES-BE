package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.ManageDTO;
import com.powersupply.PES.domain.dto.MemberDTO;
import com.powersupply.PES.domain.entity.*;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.MemberRepository;
import com.powersupply.PES.repository.ProblemRepository;
import com.powersupply.PES.repository.QuestionRepository;
import com.powersupply.PES.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageService {
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;

    // 전체 문제 리스트 불러오기
    public List<ManageDTO.ProblemList> problemList() {
        return problemRepository.findAll().stream()
                .map(ManageDTO.ProblemList::new)
                .collect(Collectors.toList());
    }

    // 특정 문제 detail 불러오기
    public ManageDTO.ProblemDetail problemDetail(Long problemId) {
        return problemRepository.findById(problemId)
                .map(ManageDTO.ProblemDetail::new)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제가 존재하지 않습니다."));
    }

    // 전체 멤버 리스트 불러오기
    @Transactional(readOnly = true)
    public List<ManageDTO.MemberList> list() {
        return memberRepository.findAll().stream()
                .map(ManageDTO.MemberList::new)
                .collect(Collectors.toList());
    }

    // 특정 멤버 detail 불러오기
    public ManageDTO.MemberDetail readDetail(String memberId) {
        return memberRepository.findById(memberId)
                .map(ManageDTO.MemberDetail::new)
                .orElseThrow(() -> new IllegalArgumentException("해당 id가 존재하지 않습니다."));
    }

    // 멤버 삭제하기
    public ResponseEntity<?> deleteMember(String memberId) {
        String id = JwtUtil.getMemberIdFromToken();
        
        MemberEntity admin = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 memberId가 없음"));
        
        memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"삭제하려는 memberId가 없음"));

        if (admin.getMemberStatus() != "관리자") {
            throw new AppException(ErrorCode.FORBIDDEN,"관리자가 아님");
        } else {
            memberRepository.deleteById(memberId);

            return ResponseEntity.ok().build();
        }
    }

    public MemberEntity updateMemberStatus(String memberId, ManageDTO.MemberUpdateRequestDto updateRequestDto) {
        String id = JwtUtil.getMemberIdFromToken();
        assert id != null;

        MemberEntity admin = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 memberId가 없음"));

        Optional<MemberEntity> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND,"해당 memberId가 없음");
        }

        if (admin.getMemberStatus() != "관리자") {
            throw new AppException(ErrorCode.FORBIDDEN,"관리자가 아님");
        } else {
            MemberEntity member = optionalMember.get();
            member.setMemberStatus(updateRequestDto.getMemberStatus());

            return memberRepository.save(member);
        }
    }
}