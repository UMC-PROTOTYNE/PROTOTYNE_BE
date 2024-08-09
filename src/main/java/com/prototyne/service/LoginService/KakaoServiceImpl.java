package com.prototyne.service.LoginService;

import com.prototyne.apiPayload.code.status.ErrorStatus;
import com.prototyne.apiPayload.exception.handler.TempHandler;
import com.prototyne.converter.UserConverter;
import com.prototyne.domain.User;
import com.prototyne.repository.UserRepository;
import com.prototyne.web.dto.UserDto;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoServiceImpl implements KakaoService {
    private final JwtManager jwtManager;
    private final UserRepository userRepository;
    private String clientId;

    @Autowired
    public KakaoServiceImpl(JwtManager jwtManager, UserRepository userRepository, @Value("${spring.datasource.client-id}") String clientId) {
        this.jwtManager = jwtManager;
        this.userRepository = userRepository;
        this.clientId = clientId;
    }

    @Override
    public UserDto.KakaoTokenResponse getAccessToken(String code) {
        String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
        UserDto.KakaoTokenResponse kakaotokenresponse = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .bodyToMono(UserDto.KakaoTokenResponse.class)
                .block();
        UserDto.UserInfoResponse userinfo = getKakaoInfo(Objects.requireNonNull(kakaotokenresponse).getAccessToken());
        User user = userRepository.findByEmail(userinfo.getKakaoAccount().getEmail());
        kakaotokenresponse.setNewUser(user == null);
        if (user == null) {
            user = userRepository.save(UserConverter.toUser(userinfo));
        } else if (!user.getSignupComplete()) {
            kakaotokenresponse.setNewUser(true);
        }
        Long id = user.getId();
        String token = jwtManager.createJwt(id);
        kakaotokenresponse.setAccessToken(token);
        return kakaotokenresponse;
    }

    @Override
    public UserDto.UserInfoResponse getKakaoInfo(String accessToken) {
        return WebClient.create("https://kapi.kakao.com")
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .bodyToMono(UserDto.UserInfoResponse.class)
                .block();
    }

    @Override
    public UserDto.UserRequest getUserInfo(String accessToken) {
        Long id = jwtManager.validateJwt(accessToken);
//        log.info("id : {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new TempHandler(ErrorStatus.LOGIN_ERROR_ID));
        return UserConverter.toUserInfoDto(user);
    }

    public void signIn(String token, UserDto.UserDetailRequest request) {
        Long id = jwtManager.validateJwt(token);
        User user = userRepository.findById(id).orElseThrow(() -> new TempHandler(ErrorStatus.LOGIN_ERROR_ID));
        user.setFamilyMember(request.getFamilyMember());
        user.setGender(request.getGender());
        user.setBirth(request.getBirth());
        user.setSignupComplete(true);
        userRepository.save(user);
    }
}