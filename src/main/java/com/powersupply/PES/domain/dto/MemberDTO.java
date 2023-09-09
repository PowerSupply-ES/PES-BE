package com.powersupply.PES.domain.dto;

import lombok.Getter;

public class MemberDTO {

    @Getter
    public static class MemberJoinRequest {
        private String memberStuNum; // 학번
        private String memberPw; // 멤버 비밀번호
        private String memberName; // 이름
        private int memberCardiNum; // 기수
        private String memberMajor; // 학과
        private String memberPhone; // 전화번호
        private String memberEmail; // 이메일
        private String memberGitUrl; // 깃주소
    }
}
