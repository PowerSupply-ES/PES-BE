package com.powersupply.PES.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "problem_table")
public class ProblemEntity {

    @Id
    private Long problemId;
    private String problemTitle;
    private int problemScore;
    @Column(columnDefinition = "TEXT")
    private String context;
    @Nullable
    private int sample;
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
