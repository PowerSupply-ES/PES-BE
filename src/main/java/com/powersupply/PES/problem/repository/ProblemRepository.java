package com.powersupply.PES.problem.repository;

import com.powersupply.PES.problem.entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {
    @Query("SELECT p, a FROM ProblemEntity p LEFT JOIN p.answerEntities a ON a.memberEntity.memberId = :id")
    List<Object[]> findAllProblemsWithAnswers(@Param("id") String id);

    @Query("SELECT COUNT(DISTINCT m) FROM ProblemEntity p JOIN p.answerEntities a JOIN a.memberEntity m " +
            "WHERE p = :problemEntity AND m.memberStatus = :memberStatus AND a.answerState != null ")
    Integer countStudentsWhoSolvedProblemWithStatus(@Param("problemEntity") ProblemEntity problemEntity, @Param("memberStatus") String memberStatus);
}
