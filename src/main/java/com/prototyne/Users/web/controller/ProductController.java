package com.prototyne.Users.web.controller;


import com.prototyne.apiPayload.ApiResponse;
import com.prototyne.repository.HeartRepository;
import com.prototyne.config.JwtManager;
import com.prototyne.Users.service.ProductService.EventService;
import com.prototyne.Users.web.dto.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("usersProductController")
@RequiredArgsConstructor
@RequestMapping("/users/product")
@Tag(name = "${swagger.tag.product}")
public class ProductController {

    private final EventService eventService;
    private final JwtManager jwtManager;
    private final HeartRepository heartRepository;

    @GetMapping("/home")
    @Operation(summary = "홈 화면 조회 API - 인증 필요",
            description = """
                홈 화면 조회 \n
                인기순 2개, 마감 임박순 3개, 신규 등록순 3개 \n""",
            security = {@SecurityRequirement(name = "session-token")})
    public ApiResponse<ProductDTO.HomeResponse> getHome(HttpServletRequest token) {
        String oauthToken = jwtManager.getToken(token);
        ProductDTO.HomeResponse home = eventService.getHomeById(oauthToken);
        return ApiResponse.onSuccess(home);
    }

    @GetMapping("/list")
    @Operation(summary = "시제품 목록 조회 API - 인증 필요" ,
            description = """
                정렬 타입 입력 ("" 없이 입력) \n
                type = "popular"(인기순, 기본) | "imminent"(마감 임박순) | "new"(최신 등록순) \n
                ** 인기순: 북마크 수 + 투자 수의 평균으로 \n
                ** 마감 임박순: 신청 마감이 3일 남은 시제품만 \n
                ** 최신 등록순: 해당 주에 등록된 시제품만""",
            security = {@SecurityRequirement(name = "session-token")})
    public ApiResponse<List<ProductDTO.EventResponse>> getEventsList(
            HttpServletRequest token,
            @RequestParam(value = "type", defaultValue = "popular") String type) {
        String oauthToken = jwtManager.getToken(token);
        Long userId = jwtManager.validateJwt(oauthToken);
        List<ProductDTO.EventResponse> eventsList = eventService.getEventsByType(userId, type);
        return ApiResponse.onSuccess(eventsList);
    }

    @GetMapping("/detail/{eventId}")
    @Operation(summary = "시제품 상세보기 API - 인증 필요",
            description = "이벤트 시제품 id 입력",
            security = {@SecurityRequirement(name = "session-token")})
    public ApiResponse<ProductDTO.EventDetailsResponse> getEventDetails(
            HttpServletRequest token,
            @PathVariable("eventId") Long eventId) {

        String oauthToken = jwtManager.getToken(token);
        ProductDTO.EventDetailsResponse eventDetails = eventService.getEventDetailsById(oauthToken, eventId);
        return ApiResponse.onSuccess(eventDetails);
    }

    @GetMapping("/select")
    @Operation(summary = "시제품 카테고리 선택 조회 API - 인증 필요",
            description = """
                카테고리 입력 ("" 없이 입력) \n
                category = "뷰티" | "스포츠" | "식품" | "의류" | "전자기기" | "장난감" \n""",
            security = {@SecurityRequirement(name = "session-token")})
    public ApiResponse<List<ProductDTO.SearchResponse>> getCategoriesList(
            HttpServletRequest token,
            @RequestParam(value = "category") String category) {
        String oauthToken = jwtManager.getToken(token);
        List<ProductDTO.SearchResponse> categoriesList = eventService.getEventsByCategory(oauthToken, category);
        return ApiResponse.onSuccess(categoriesList);
    }
}
