package com.prototyne.web.controller;

import com.prototyne.apiPayload.ApiResponse;
import com.prototyne.service.LoginService.KakaoServiceImpl;
import com.prototyne.web.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class LoginController {
    private final KakaoServiceImpl kakaoService;

    @Tag(name = "${swagger.tag.auth}")
    @GetMapping("/login")
    @Operation(summary = "로그인 API",
            description = "Access Token 응답")
    public ApiResponse<UserDto.KakaoTokenResponse> callback(@RequestParam("code") String code) {
        UserDto.KakaoTokenResponse accessToken = kakaoService.getAccessToken(code);
        return ApiResponse.onSuccess(accessToken);
    }

    @Tag(name = "${swagger.tag.auth}")
    @PostMapping("/signup")
    @Operation(summary = "회원가입 API - 인증 필요",
            description = "회원가입 API - 인증 필요",
            security = {@SecurityRequirement(name = "session-token")})
    public ApiResponse<String> userInfo(HttpServletRequest token, @RequestBody @Valid UserDto.UserDetailRequest request) {
        String aouthtoken = token.getHeader("Authorization").replace("Bearer ", "");
        kakaoService.signIn(aouthtoken, request);
        return ApiResponse.onSuccess("회원가입 완료");
    }

    @Tag(name = "${swagger.tag.my}")
    @GetMapping("/user")
    @Operation(summary = "사용자 정보 조회 API - 인증 필요",
            description = "사용자 정보 조회 API - 인증 필요",
            security = {@SecurityRequirement(name = "session-token")})
    public ApiResponse<UserDto.UserRequest> userInfo(HttpServletRequest token) {
        String aouthtoken = token.getHeader("Authorization").replace("Bearer ", "");
        return ApiResponse.onSuccess(kakaoService.getUserInfo(aouthtoken));
    }
}