package com.powersupply.PES.domain.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberDTO {

    @Getter
    public static class MemberSignUpRequest {
        private String memberStuNum; // 학번
        private String memberPw; // 멤버 비밀번호
        private String memberName; // 이름
        private int memberCardiNum; // 기수
        private String memberMajor; // 학과
        private String memberPhone; // 전화번호
        private String memberEmail; // 이메일
        private String memberGitUrl; // 깃주소
    }

    @Getter
    public static class MemberSignInRequest {
        private String memberStuNum; // 학번
        private String memberPw; // 비밀번호
    }

    @Builder
    @Getter
    public static class MemberMyPageResponse {
        private String memberStuNum; // 학번
        private String memberName; // 이름
        private int memberCardiNum; // 기수
        private String memberMajor; // 학과
        private String memberPhone; // 전화번호
        private String memberStatus; // 상태
        private String memberEmail; // 이메일
        private int memberScore; // 점수
        private String memberGitUrl; // 깃주소
    }

    @Getter
    public static class MemberFindPwRequest {
        private String memberStuNum;
        private String memberName;
    }

    @Getter
    @Builder
    public static class MemberMyUserResponse {
        private String memberName;
        private int memberScore;
    }
}
