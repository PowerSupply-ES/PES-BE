package com.powersupply.PES.repository;

import com.powersupply.PES.domain.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
//    Optional<AnswerEntity> findByMemberEntity_MemberStuNumAndProblemEntity_ProblemId(String stuNum, Long problemId);

    List<AnswerEntity> findAllByProblemEntity_ProblemId(Long problemId);

    // 이메일을 기반으로 finalScore의 합계를 계산
    @Query("SELECT SUM(a.finalScore) FROM AnswerEntity a WHERE a.memberEntity.memberEmail = :email")
    Integer sumFinalScoreByMemberEmail(@Param("email") String email);

    Optional<AnswerEntity> findByMemberEntity_MemberEmailAndProblemEntity_ProblemId(String email, Long problemId);

    List<AnswerEntity> findAllByMemberEntity_MemberEmail(String email);

    @Query("SELECT SUM(a.finalScore) FROM AnswerEntity a WHERE a.memberEntity.memberId = :id")
    Integer sumFinalScoreById(@Param("id") String id);

    List<AnswerEntity> findAllByMemberEntity_MemberId(String id);
}
