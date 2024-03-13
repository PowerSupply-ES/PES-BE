package com.powersupply.PES.repository;

import com.powersupply.PES.domain.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {

    List<AnswerEntity> findAllByProblemEntity_ProblemId(Long problemId);

    @Query("SELECT SUM(a.finalScore) FROM AnswerEntity a WHERE a.memberEntity.memberId = :id")
    Integer sumFinalScoreById(@Param("id") String id);

    List<AnswerEntity> findAllByMemberEntity_MemberId(String id);

    Optional<AnswerEntity> findByMemberEntity_MemberIdAndProblemEntity_ProblemId(String id, Long problemId);
}
