package com.powersupply.PES.problem.entity;

import com.powersupply.PES.answer.entity.AnswerEntity;
import com.powersupply.PES.question.entity.QuestionEntity;
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
@Table(name = "problem_table")
public class ProblemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;
    private String problemTitle;
    private int problemScore;
    @Column(columnDefinition = "TEXT")
    private String context;
    @Column(nullable = true)
    private Integer sample;
    @Column(columnDefinition = "TEXT")
    private String inputs;
    @Column(columnDefinition = "TEXT")
    private String outputs;

    @Builder.Default
    @OneToMany(mappedBy = "problemEntity")
    private List<QuestionEntity> questionEntities = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "problemEntity")
    private List<AnswerEntity> answerEntities = new ArrayList<>();
}
