package com.prototyne.Users.web.dto;

import lombok.*;


@Getter
@Builder
public class FeedbackDTO {


    private Integer answer1; //객관식 1번

    private Integer answer2; //객관식 2번

    private Integer answer3; //객관식 3번

    private Integer answer4; //객관식 4번

    private String answer5; //주관식 5번

    private Boolean answer6; //재사용 유무 Y/N


}
