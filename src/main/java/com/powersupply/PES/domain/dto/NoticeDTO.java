package com.powersupply.PES.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class NoticeDTO {

    @Builder
    @Getter
    public static class Notice {
        private String content;
        private String title;
        private int writerGen;
        private String writer;
        private boolean isImportant;
        private int noticeHit;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdTime;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedTime;

        public boolean isIsImportant() {
            return isImportant;
        }
    }

    @Getter
    public static class BaseNotice {
        private String title;
        private String content;
        private boolean isImportant;

        public boolean isIsImportant() {
            return isImportant;
        }
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
        private boolean isNewNotice;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdTime;

        public boolean isIsImportant() {
            return isImportant;
        }
        public boolean isIsNewNotice() {
            return isNewNotice;
        }
    }
}
