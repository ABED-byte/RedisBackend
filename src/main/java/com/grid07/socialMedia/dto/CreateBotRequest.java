package com.grid07.socialMedia.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateBotRequest {
    private String name;
    private String personaDescription;
}
