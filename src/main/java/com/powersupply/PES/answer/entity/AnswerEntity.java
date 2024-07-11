package com.powersupply.PES.answer.entity;

import com.powersupply.PES.comment.entity.CommentEntity;
import com.powersupply.PES.entity.BaseEntity;
import com.powersupply.PES.problem.entity.ProblemEntity;
import com.powersupply.PES.question.entity.QuestionEntity;
import com.powersupply.PES.member.entity.MemberEntity;
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

    @Lob
    private String answerFst;
    @Lob
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
