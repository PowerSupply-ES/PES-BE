package com.powersupply.PES.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class MemberDTO {

    @Getter
    @Schema(description = "회원 가입 요청 데이터")
    public static class MemberSignUpRequest {
        @Schema(description = "아이디", example = "user123")
        private String memberId; // 아이디

        @Schema(description = "비밀번호", example = "password123")
        private String memberPw; // 멤버 비밀번호

        @Schema(description = "이름", example = "홍길동")
        private String memberName; // 이름

        @Schema(description = "기수", example = "1")
        private int memberGen; // 기수

        @Schema(description = "학과", example = "컴퓨터공학과")
        private String memberMajor; // 학과

        @Schema(description = "전화번호", example = "010-1234-5678")
        private String memberPhone; // 전화번호

        @Schema(description = "이메일", example = "user@example.com")
        private String memberEmail; // 이메일
    }

    @Getter
    @Schema(description = "회원 로그인 요청 데이터")
    public static class MemberSignInRequest {
        @Schema(description = "아이디", example = "user123")
        private String memberId; // 아이디

        @Schema(description = "비밀번호", example = "password123")
        private String memberPw; // 비밀번호
    }

    @Builder
    @Getter
    @Schema(description = "마이페이지 응답 데이터")
    public static class MemberMyPageResponse {
        @Schema(description = "이름", example = "홍길동")
        private String memberName; // 이름

        @Schema(description = "기수", example = "1")
        private int memberGen; // 기수

        @Schema(description = "학과", example = "컴퓨터공학과")
        private String memberMajor; // 학과

        @Schema(description = "전화번호", example = "010-1234-5678")
        private String memberPhone; // 전화번호

        @Schema(description = "상태", example = "재학생")
        private String memberStatus; // 상태

        @Schema(description = "이메일", example = "user@example.com")
        private String memberEmail; // 이메일

        @Schema(description = "점수", example = "100")
        private int memberScore; // 점수

        @Schema(description = "아이디", example = "user123")
        private String memberId; // 아이디
    }

    @Getter
    @Builder
    @Schema(description = "이름과 점수 응답 데이터")
    public static class NameScoreResponse {
        @Schema(description = "이름", example = "홍길동")
        private String memberName;

        @Schema(description = "상태", example = "재학생")
        private String memberStatus;

        @Schema(description = "점수", example = "100")
        private int memberScore;

        @Schema(description = "기수", example = "1")
        private int memberGen;

        @Schema(description = "새로운 공지 여부", example = "true")
        private boolean hasNewNotices;
    }

    @Getter
    @Builder
    @Schema(description = "내가 푼 문제 응답 데이터")
    public static class MemberMySolveResponse {
        @Schema(description = "문제 ID", example = "1")
        private Long problemId;

        @Schema(description = "문제 제목", example = "문제 제목 예시")
        private String problemTitle;

        @Schema(description = "문제 점수", example = "10")
        private int problemScore;

        @Schema(description = "답변 ID", example = "1")
        private Long answerId;

        @Schema(description = "답변 상태", example = "완료")
        private String answerState;

        @Schema(description = "최종 점수", example = "10")
        private int finalScore;
    }

    @Getter
    @Builder
    @Schema(description = "내가 받은 피드백 응답 데이터")
    public static class MemberMyFeedbackResponse {
        @Schema(description = "답변 ID", example = "1")
        private Long answerId;

        @Schema(description = "기수", example = "1")
        private int memberGen;

        @Schema(description = "이름", example = "홍길동")
        private String memberName;

        @Schema(description = "피드백 통과 여부", example = "1")
        private int commentPassFail;

        @Schema(description = "피드백 내용", example = "잘했습니다.")
        private String commentContent;
    }

    @Getter
    @Builder
    @Setter
    @Schema(description = "랭킹 데이터")
    public static class Rank {
        @Schema(description = "순위", example = "1")
        private int rank;

        @Schema(description = "이름", example = "홍길동")
        private String memberName;

        @Schema(description = "점수", example = "100")
        private int score;
    }
}
