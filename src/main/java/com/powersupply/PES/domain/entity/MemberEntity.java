package com.powersupply.PES.domain.entity;

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
    @Column(name = "memberEmail")
    private String memberEmail; // 이메일

    @Column(name = "memberName")
    private String memberName; // 이름

    @Column(name = "memberGen")
    private int memberGen; // 기수

    @Column(name = "memberBeakId")
    private String memberBaekId; // 백준 아이디

    @Column(name = "memberStatus")
    private String memberStatus; // 상태(학생/관리자)

    @Column(name = "memberPw")
    private String memberPw; // 암호화된 비밀번호

    @Column(name = "memberMajor")
    private String memberMajor; // 학과

    @Column(name = "memberPhone", unique = true)
    private String memberPhone; // 전화번호

    @Builder.Default
    @OneToMany(mappedBy = "memberEntity")
    private List<AnswerEntity> answerEntities = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "memberEntity")
    private List<CommentEntity> commentEntities = new ArrayList<>();
}
