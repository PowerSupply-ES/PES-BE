package com.powersupply.PES.domain.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "question_table")
public class QuestionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;
    private String questionContent;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "problem_id")
    private ProblemEntity problemEntity;

    @Builder.Default
    @OneToMany(mappedBy = "questionFst")
    private List<AnswerEntity> answerEntitiesForFst = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "questionSec")
    private List<AnswerEntity> answerEntitiesForSec = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
