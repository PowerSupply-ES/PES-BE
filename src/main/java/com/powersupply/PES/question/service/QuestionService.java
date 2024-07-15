package com.powersupply.PES.question.service;

import com.powersupply.PES.question.dto.QuestionDTO;
import com.powersupply.PES.question.entity.QuestionEntity;
import com.powersupply.PES.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    // (재)질문 목록 보기
    public List<QuestionDTO.QuestionList> getQeustionList(Long problemId) {
        List<QuestionEntity> questionEntityList = questionRepository.findByProblemEntity_ProblemId(problemId);
        List<QuestionDTO.QuestionList> questionLists = new ArrayList<>();

        for(QuestionEntity questionEntity: questionEntityList) {
            LocalDateTime updateTime = questionEntity.getUpdatedTime();
            if(updateTime == null){
                updateTime = questionEntity.getCreatedTime();
            }
            QuestionDTO.QuestionList questionList = QuestionDTO.QuestionList.builder()
                    .questionId(questionEntity.getQuestionId())
                    .questionContent(questionEntity.getQuestionContent())
                    .updateTime(updateTime)
                    .build();
            questionLists.add(questionList);
        }
        return questionLists;
    }
}
