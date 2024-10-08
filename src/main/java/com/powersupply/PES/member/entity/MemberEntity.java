package com.powersupply.PES.member.entity;

import com.powersupply.PES.comment.entity.CommentEntity;
import com.powersupply.PES.answer.entity.AnswerEntity;
import com.powersupply.PES.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "member_table")
public class MemberEntity extends BaseEntity {

    @Id
    @Column(name = "memberId")
    private String memberId; // 학번(ID)

    @Column(name = "memberEmail", unique = true)
    private String memberEmail; // 이메일

    @Column(name = "memberName")
    private String memberName; // 이름

    @Column(name = "memberGen")
    private int memberGen; // 기수

    @Column(name = "memberStatus")
    private String memberStatus; // 상태(학생/관리자)

    @Column(name = "memberPw")
    private String memberPw; // 암호화된 비밀번호

    @Column(name = "memberMajor")
    private String memberMajor; // 학과

    @Column(name = "memberPhone", unique = true)
    private String memberPhone; // 전화번호

    @Builder.Default
    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerEntity> answerEntities = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "memberEntity")
    private List<CommentEntity> commentEntities = new ArrayList<>();

    public void update(String memberStatus) {
        this.memberStatus = memberStatus;
    }
}
