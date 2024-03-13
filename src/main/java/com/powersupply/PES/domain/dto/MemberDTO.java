package com.powersupply.PES.domain.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberDTO {

    @Getter
    public static class MemberSignUpRequest {
        private String memberId; // 백준 아이디
        private String memberPw; // 멤버 비밀번호
        private String memberName; // 이름
        private int memberGen; // 기수
        private String memberMajor; // 학과
        private String memberPhone; // 전화번호
        private String memberEmail; // 이메일
    }

    @Getter
    public static class MemberSignInRequest {
        private String memberId; // 이메일
        private String memberPw; // 비밀번호
    }

    @Builder
    @Getter
    public static class MemberMyPageResponse {
        private String memberName; // 이름
        private int memberGen; // 기수
        private String memberMajor; // 학과
        private String memberPhone; // 전화번호
        private String memberStatus; // 상태
        private String memberEmail; // 이메일
        private int memberScore; // 점수
        private String memberId; // 백준 아이디
    }

    @Getter
    @Builder
    public static class NameScoreResponse {
        private String memberName;
        private String memberStatus;
        private int memberScore;
        private int memberGen;
    }

    @Getter
    @Builder
    public static class MemberMySolveResponse {
        private Long problemId;
        private String problemTitle;
        private int problemScore;
        private Long answerId;
        private String answerState;
        private int finalScore;
    }

    @Getter
    @Builder
    public static class MemberMyFeedbackResponse {
        private Long answerId;
        private int memberGen;
        private String memberName;
        private int commentPassFail;
        private String commentContent;
    }
}
