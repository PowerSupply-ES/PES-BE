package com.powersupply.PES.domain.dto;

import lombok.Getter;

public class NoticeDTO {

    @Getter
    public static class createNotice {
        private String title;
        private String content;
        private boolean isImportant;
    }
}
