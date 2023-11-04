package com.powersupply.PES.domain.dto;

import lombok.Builder;
import lombok.Getter;

public class ManageDTO {

    @Getter
    public static class makeQuestion {
        private String questionContent;
        private int questionDifficulty;
    }

    @Getter
    @Builder
    public static class MemberList {
        private String memberStuNum; // 학번
        private String memberName; // 이름
        private int memberGen; // 기수
        private String memberMajor; // 학과
        private String memberStatus; // 상태
        private String memberEmail; // 이메일
    }
}
