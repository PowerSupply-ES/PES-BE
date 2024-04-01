package com.powersupply.PES.domain.dto;

import lombok.Builder;
import lombok.Getter;

public class AnswerDTO {

    @Getter
    public static class AnswerContent {
        private String answerFst;
        private String answerSec;
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
        private int memberGen;
        private String memberName;
        private int commentCount;
        private String answerState;
    }
}
