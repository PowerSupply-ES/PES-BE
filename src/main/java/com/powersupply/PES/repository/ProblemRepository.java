package com.powersupply.PES.repository;

import com.powersupply.PES.domain.entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {
    @Query("SELECT p, a FROM ProblemEntity p LEFT JOIN p.answerEntities a ON a.memberEntity.memberEmail = :email")
    List<Object[]> findAllProblemsWithAnswers(@Param("email") String email);

    @Query("SELECT COUNT(DISTINCT m) FROM ProblemEntity p JOIN p.answerEntities a JOIN a.memberEntity m " +
            "WHERE p = :problemEntity AND m.memberStatus = :memberStatus AND a.answerState != null ")
    Integer countStudentsWhoSolvedProblemWithStatus(@Param("problemEntity") ProblemEntity problemEntity, @Param("memberStatus") String memberStatus);
}
