package com.powersupply.PES.problem.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ProblemDTO {

    @Getter
    @Builder
    public static class ProblemResponse {
        private Long problemId;
        private String problemTitle;
        private int problemScore;
        private int answerCount;
        private Long answerId;
        private String answerState;
        private Integer myScore;
    }
}
