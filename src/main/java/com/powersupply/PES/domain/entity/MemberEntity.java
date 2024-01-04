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
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long memberId; // 멤버 id

    @Column(name = "memberName")
    private String memberName; // 이름

    @Column(name = "memberGen")
    private int memberGen; // 기수

    @Column(name = "memberBeakId")
    private String memberBaekId; // 백준 아이디

    @Column(name = "memberStatus")
    private String memberStatus; // 상태(학생/관리자)

    @OneToOne(mappedBy = "memberEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DetailMemberEntity detailMemberEntity;

    @Builder.Default
    @OneToMany(mappedBy = "memberEntity")
    private List<AnswerEntity> answerEntities = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "memberEntity")
    private List<CommentEntity> commentEntities = new ArrayList<>();
}
