package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.AnswerDTO;
import com.powersupply.PES.domain.entity.AnswerEntity;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.AnswerRepository;
import com.powersupply.PES.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public void saveAnswer(Long problemId, String memberStuNum, AnswerDTO.answerRequest dto) {
        if(!JwtUtil.getMemberStuNumFromToken().equals(memberStuNum)) {
            throw new AppException(ErrorCode.INVALID_INPUT,"답변할 수 권한이 없는 유저 입니다.");
        }

        AnswerEntity answerEntity = answerRepository.findByMemberEntity_MemberStuNumAndProblemEntity_ProblemId(memberStuNum,problemId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "아직 채점되지 않았습니다."));

        answerEntity.setAnswerState("UnderReview");
        answerEntity.setAnswerFst(dto.getAnswerFst());
        answerEntity.setAnswerSec(dto.getAnswerSec());
        answerRepository.save(answerEntity);
    }
}
