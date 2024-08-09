package com.prototyne.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class TicketDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketListDto {
        private LocalDateTime createdAt;
        private String name;
        private String ticketDesc;
        private Integer ticketChange;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketNumberDto {
        private Integer ticketNumber;
    }
}
