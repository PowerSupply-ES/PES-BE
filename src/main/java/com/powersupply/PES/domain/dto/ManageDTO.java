package com.powersupply.PES.domain.dto;

import lombok.Getter;

public class ManageDTO {

    @Getter
    public static class makeQuestion {
        private String questionContent;
        private int questionDifficulty;
    }
}
