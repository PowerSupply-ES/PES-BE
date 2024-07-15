package com.powersupply.PES.question.repository;

import com.powersupply.PES.question.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity,Long> {
    List<QuestionEntity> findByProblemEntity_ProblemId(Long problemId);
}
