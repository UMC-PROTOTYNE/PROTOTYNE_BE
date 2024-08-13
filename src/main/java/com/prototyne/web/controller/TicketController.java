package com.prototyne.web.controller;

import com.prototyne.apiPayload.ApiResponse;
import com.prototyne.service.LoginService.JwtManager;
import com.prototyne.service.TicketService.TicketService;
import com.prototyne.web.dto.TicketDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket")
@Tag(name = "${swagger.tag.my-etc}")
public class TicketController {

    private final TicketService ticketService;
    private final JwtManager jwtManager;

    @GetMapping
    @Operation(summary = "티켓 개수 조회 API - 인증 필요",
            description = "티켓 개수 조회",
            security = {@SecurityRequirement(name = "session-token")})
    public ApiResponse<TicketDto.TicketNumberDto> getTicket(HttpServletRequest token) {
        String aouthtoken = jwtManager.getToken(token);
        TicketDto.TicketNumberDto ticketNumber = ticketService.getTicketNumber(aouthtoken);
        return ApiResponse.onSuccess(ticketNumber);
    }

    @GetMapping("/all")
    @Operation(summary = "티켓 전체 내역 조회 API - 인증 필요",
            description = """
                    티켓 전체 내역 조회
                    - startDate: 조회 시작일 (yyyy-MM-dd)
                    - endDate: 조회 종료일 (yyyy-MM-dd)
                    """,
            security = {@SecurityRequirement(name = "session-token")})
    public ApiResponse<List<TicketDto.TicketListDto>> getTicketList(HttpServletRequest token, @RequestParam String startDate, @RequestParam String endDate) {
        String aouthtoken = jwtManager.getToken(token);
        return ApiResponse.onSuccess(ticketService.getTicketDateList(aouthtoken, startDate, endDate, false));
    }

    @GetMapping("/used")
    @Operation(summary = "티켓 사용 내역 조회 API - 인증 필요",
            description = """
                    티켓 사용 내역 조회
                    - startDate: 조회 시작일 (yyyy-MM-dd)
                    - endDate: 조회 종료일 (yyyy-MM-dd)
                    """,
            security = {@SecurityRequirement(name = "session-token")})
    public ApiResponse<List<TicketDto.TicketListDto>> getTicketListUsed(HttpServletRequest token,@RequestParam String startDate, @RequestParam String endDate) {
        String aouthtoken = jwtManager.getToken(token);
        List<TicketDto.TicketListDto> ticketList = ticketService.getTicketDateList(aouthtoken, startDate, endDate, true);
        return ApiResponse.onSuccess(ticketList);
    }
}
