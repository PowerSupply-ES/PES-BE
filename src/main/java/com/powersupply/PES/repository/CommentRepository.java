package com.powersupply.PES.repository;

import com.powersupply.PES.domain.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
    Optional<List<CommentEntity>> findByAnswerEntity_AnswerId(Long answerId);

    List<CommentEntity> findAllByMemberEntity_MemberId(String id);

    @Query("SELECT COUNT(c) AS count " +
            "FROM MemberEntity m " +
            "LEFT JOIN CommentEntity c ON m.memberId = c.memberEntity.memberId " +
            "WHERE m.memberId = :id")
    Integer findMemberCommentsCount(@Param("id") String id);
}
