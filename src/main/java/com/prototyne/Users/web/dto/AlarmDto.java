package com.prototyne.Users.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDto {
    private String title;
    private String contents;
    private Integer dateAgo;
    private String thumbnailUrl;
}
