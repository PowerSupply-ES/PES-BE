package com.powersupply.PES.domain.dto;

import com.powersupply.PES.domain.entity.MemberEntity;
import com.powersupply.PES.domain.entity.ProblemEntity;
import com.powersupply.PES.domain.entity.QuestionEntity;
import com.powersupply.PES.exception.ExceptionManger;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ManageDTO {

    // 문제 관리 기능 관련
    @Getter
    public static class ProblemList {
        private Long problemId;
        private String problemTitle;
        private int problemScore;

        @Builder
        public ProblemList(ProblemEntity problem) {
            this.problemId = problem.getProblemId();
            this.problemTitle = problem.getProblemTitle();
            this.problemScore = problem.getProblemScore();
        }
    }

    @Getter
    public static class ProblemDetail {
        private Long problemId;
        private String problemTitle;
        private int problemScore;
        private String context;
        private int sample;
        private String inputs;
        private String outputs;

        @Builder
        public ProblemDetail(ProblemEntity problem) {
            this.problemId = problem.getProblemId();
            this.problemTitle = problem.getProblemTitle();
            this.problemScore = problem.getProblemScore();
            this.context = problem.getContext();
            this.sample = problem.getSample();
            this.inputs = problem.getInputs();
            this.outputs = problem.getOutputs();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProblemRequestDto {
        private String problemTitle;
        private int problemScore;
        private String context;
        private int sample;
        private String inputs;
        private String outputs;
    }

    // 회원 관리 기능 관련
    @Getter
    public static class MemberList {
        private String memberId; // 학번
        private String memberName; // 이름
        private String memberMajor; // 학과
        private String memberStatus; // 상태

        @Builder
        public MemberList(MemberEntity member) {
            this.memberId = member.getMemberId();
            this.memberName = member.getMemberName();
            this.memberMajor = member.getMemberMajor();
            this.memberStatus = member.getMemberStatus();
        }
    }

    @Getter
    public static class MemberDetail {
        private String memberId; // 학번
        private String memberName; // 이름
        private int memberGen; // 기수
        private String memberMajor; // 학과
        private String memberStatus; // 상태
        private String memberEmail; // 이메일

        @Builder
        public MemberDetail(MemberEntity member) {
            this.memberId = member.getMemberId();
            this.memberName = member.getMemberName();
            this.memberGen = member.getMemberGen();
            this.memberMajor = member.getMemberMajor();
            this.memberStatus = member.getMemberStatus();
            this.memberEmail = member.getMemberEmail();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class MemberUpdateRequestDto {
        private String memberStatus;
    }

    // 질문 관리 기능 관련

    @Getter
    public static class QuestionList {
        private Long questionId;
        private String questionContent;

        @Builder
        public QuestionList(QuestionEntity question) {
            this.questionId = question.getQuestionId();
            this.questionContent = question.getQuestionContent();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class QuestionRequestDto {
        private String questionContent;
    }
}
