package com.prototyne.service.TicketService;

import com.prototyne.repository.TicketRepository;
import com.prototyne.service.LoginService.JwtManager;
import com.prototyne.web.dto.TicketDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final JwtManager jwtManager;
    private final TicketRepository ticketRepository;

    public List<TicketDto.TicketListDto> getTicketList(String accessToken) {
        Long id = jwtManager.validateJwt(accessToken);

        return ticketRepository.findByUserId(id).stream().sorted((o2, o1) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .map(ticket -> TicketDto.TicketListDto.builder()
                        .name(ticket.getName())
                        .createdAt(ticket.getCreatedAt())
                        .ticketDesc(ticket.getTicketDesc())
                        .ticketChange(ticket.getTicketChange())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public TicketDto.TicketNumberDto getTicketNumber(String accessToken) {
        AtomicReference<Integer> ticketNumber = new AtomicReference<>(0);
        getTicketList(accessToken).forEach(ticket -> ticketNumber.updateAndGet(v -> v + ticket.getTicketChange()));
        return TicketDto.TicketNumberDto.builder()
                .ticketNumber(ticketNumber.get())
                .build();
    }

}
