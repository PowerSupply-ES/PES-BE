package com.powersupply.PES.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "answer_table")
public class AnswerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;
    private String answerState;
    private String answerFst;
    private String answerSec;
    private int finalScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private MemberEntity memberEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "problemId")
    private ProblemEntity problemEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionFst")
    private QuestionEntity questionFst;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionSec")
    private QuestionEntity questionSec;

    @Builder.Default
    @OneToMany(mappedBy = "answerEntity")
    private List<CommentEntity> commentEntities = new ArrayList<>();
}
