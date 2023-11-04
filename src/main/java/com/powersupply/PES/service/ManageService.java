package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.ManageDTO;
import com.powersupply.PES.domain.dto.MemberDTO;
import com.powersupply.PES.domain.entity.MemberEntity;
import com.powersupply.PES.domain.entity.ProblemEntity;
import com.powersupply.PES.domain.entity.QuestionEntity;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.MemberRepository;
import com.powersupply.PES.repository.ProblemRepository;
import com.powersupply.PES.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageService {

    private final ProblemRepository problemRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;

    // 질문 만들기
    @Transactional
    public void makeQuestion(Long problemId, ManageDTO.makeQuestion dto) {

        ProblemEntity problemEntity = problemRepository.findById(problemId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"해당 문제가 없습니다."));

        QuestionEntity questionEntity = QuestionEntity.builder()
                .questionContent(dto.getQuestionContent())
                .questionDifficulty(dto.getQuestionDifficulty())
                .problemEntity(problemEntity)
                .build();
        questionRepository.save(questionEntity);
    }

    // 멤버 관리하기
    @Transactional
    public List<ManageDTO.MemberList> getMemberList() {

        List<MemberEntity> memberEntityList = memberRepository.findAll();
        List<ManageDTO.MemberList> memberListArrayList = new ArrayList<>();

        for(MemberEntity memberEntity: memberEntityList) {
            ManageDTO.MemberList memberList = ManageDTO.MemberList.builder()
                    .memberStuNum(memberEntity.getMemberStuNum())
                    .memberGen(memberEntity.getMemberGen())
                    .memberName(memberEntity.getMemberName())
                    .memberMajor(memberEntity.getDetailMemberEntity().getMemberMajor())
                    .memberEmail(memberEntity.getDetailMemberEntity().getMemberEmail())
                    .memberStatus(memberEntity.getMemberStatus())
                    .build();
            memberListArrayList.add(memberList);
        }

        return memberListArrayList;
    }
}
