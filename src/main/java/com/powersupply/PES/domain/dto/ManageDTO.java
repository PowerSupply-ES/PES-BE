package com.powersupply.PES.domain.dto;

import com.powersupply.PES.domain.entity.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ManageDTO {

    @Getter
    public static class makeQuestion {
        private String questionContent;
        private int questionDifficulty;
    }

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
    public static class MemberResponseDto {
        private String memberId;
        private String memberStatus;

        public MemberResponseDto(MemberEntity member) {
            this.memberId = member.getMemberId();
            this.memberStatus = member.getMemberStatus();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class MemberUpdateRequestDto {
        private String memberStatus;
        @Builder
        public MemberUpdateRequestDto(String memberStatus) {
            this.memberStatus = memberStatus;
        }
    }

    @Getter
    public static class PatchMember {
        private int memberGen;
        private String memberStatus;
    }
}
