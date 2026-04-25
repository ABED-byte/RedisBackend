package com.grid07.social.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCommentRequest {
    private Long authorId;
    private String authorType;
    private String content;
    private int depthLevel;
}
