package com.powersupply.PES.domain.dto;

import lombok.Builder;
import lombok.Getter;

public class NoticeDTO {

    @Getter
    public static class CreateNotice {
        private String title;
        private String content;
        private boolean isImportant;
    }

    @Getter
    @Builder
    public static class NoticeList {
        private Long noticeId;
        private String title;
        private int writerGen;
        private String writer;
        private boolean isImportant;
        private int noticeHit;
    }
}
