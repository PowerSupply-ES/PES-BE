package com.powersupply.PES.service;

import com.powersupply.PES.domain.dto.ManageDTO;
import com.powersupply.PES.domain.entity.ProblemEntity;
import com.powersupply.PES.domain.entity.QuestionEntity;
import com.powersupply.PES.exception.AppException;
import com.powersupply.PES.exception.ErrorCode;
import com.powersupply.PES.repository.ProblemRepository;
import com.powersupply.PES.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManageService {

    private final ProblemRepository problemRepository;
    private final QuestionRepository questionRepository;

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
}
