package com.powersupply.PES.mapper;

import com.powersupply.PES.member.dto.MemberDTO;
import com.powersupply.PES.answer.entity.AnswerEntity;
import com.powersupply.PES.comment.entity.CommentEntity;

public class MapperUtils {

    public static MemberDTO.MemberMySolveResponse toMemberMySolveResponse(AnswerEntity answerEntity) {
        return MemberDTO.MemberMySolveResponse.builder()
                .problemId(answerEntity.getProblemEntity().getProblemId())
                .problemTitle(answerEntity.getProblemEntity().getProblemTitle())
                .problemScore(answerEntity.getProblemEntity().getProblemScore())
                .answerId(answerEntity.getAnswerId())
                .answerState(answerEntity.getAnswerState())
                .finalScore(answerEntity.getFinalScore())
                .build();
    }

    public static MemberDTO.MemberMyFeedbackResponse toMemberMyFeedbackResponse(CommentEntity commentEntity) {
        return MemberDTO.MemberMyFeedbackResponse.builder()
                .answerId(commentEntity.getAnswerEntity().getAnswerId())
                .memberGen(commentEntity.getAnswerEntity().getMemberEntity().getMemberGen())
                .memberName(commentEntity.getAnswerEntity().getMemberEntity().getMemberName())
                .commentPassFail(commentEntity.getCommentPassFail())
                .commentContent(commentEntity.getCommentContent())
                .build();
    }
}
