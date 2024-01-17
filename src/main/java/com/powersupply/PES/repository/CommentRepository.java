package com.powersupply.PES.repository;

import com.powersupply.PES.domain.entity.AnswerEntity;
import com.powersupply.PES.domain.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
    Optional<List<CommentEntity>> findByAnswerEntity_AnswerId(Long answerId);

//    Optional<CommentEntity> findByAnswerEntityAndMemberEntity_MemberStuNum(AnswerEntity answerEntity, String stuNum);

//    List<CommentEntity> findByMemberEntity_MemberStuNum(String memberStuNum);
}
