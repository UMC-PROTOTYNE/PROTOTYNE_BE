package com.prototyne.service.TicketService;

import com.prototyne.web.dto.TicketDto;

import java.util.List;

public interface TicketService {
    TicketDto.TicketNumberDto getTicketNumber(String accessToken);

    List<TicketDto.TicketListDto> getTicketList(String accessToken);

    List<TicketDto.TicketListDto> getTicketDateList(String accessToken, String startDate, String endDate, boolean used);

    void buyTicket(String accessToken, int ticketNumber);
}
