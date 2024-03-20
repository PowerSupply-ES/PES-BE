package com.powersupply.PES.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CommentDTO {

    @Getter
    @Builder
    public static class GetComment {
        private String writerId;
        private String writerName;
        private int writerGen;
        private String commentContent;
        private int commentPassFail;
    }

    @Getter
    public static class CreateComment {
        private String comment;
        private int commentPassFail;
    }
}
