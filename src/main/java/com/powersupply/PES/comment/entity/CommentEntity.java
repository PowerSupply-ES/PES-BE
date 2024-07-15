package com.powersupply.PES.comment.entity;

import com.powersupply.PES.answer.entity.AnswerEntity;
import com.powersupply.PES.entity.BaseEntity;
import com.powersupply.PES.member.entity.MemberEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "comment_table")
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private int commentPassFail;

    @Lob
    private String commentContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private MemberEntity memberEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answerId")
    private AnswerEntity answerEntity;
}
