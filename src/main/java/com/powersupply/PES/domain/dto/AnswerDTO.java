package com.powersupply.PES.domain.dto;

import lombok.Builder;
import lombok.Getter;

public class AnswerDTO {

    @Getter
    public static class AnswerContent {
        private String answerFst;
        private String answerSec;
    }

    @Getter
    public static class gitUrl {
        private String answerUrl;
    }

    @Getter
    @Builder
    public static class SolveList {
        private String memberStuNum;
        private String memberName;
        private int commentCount;
        private String answerState;
    }

    @Getter
    public static class returnSubmit {
        private Long answerId;
        private int answerState;
    }

    @Builder
    @Getter
    public static class GetAnswerId {
        private Long answerId;
    }

    @Builder
    @Getter
    public static class GetAnswer {
        private String questionContentFst;
        private String questionContentSec;
        private String answerFst;
        private String answerSec;
        private String answerState;
    }

    @Builder
    @Getter
    public static class GetAnswerList {
        private Long answerId;
        private String memberEmail;
        private int commentCount;
    }
}
