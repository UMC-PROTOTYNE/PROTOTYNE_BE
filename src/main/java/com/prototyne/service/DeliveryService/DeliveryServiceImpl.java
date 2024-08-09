package com.prototyne.service.DeliveryService;

import com.prototyne.apiPayload.code.status.ErrorStatus;
import com.prototyne.apiPayload.exception.handler.TempHandler;
import com.prototyne.domain.User;
import com.prototyne.repository.UserRepository;
import com.prototyne.service.LoginService.JwtManager;
import com.prototyne.web.dto.DeliveryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.prototyne.converter.UserConverter.toDeliveryDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final UserRepository userRepository;
    private final JwtManager jwtManager;

    @Autowired
    public DeliveryServiceImpl(JwtManager jwtManager, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtManager = jwtManager;
    }

    @Override
    public DeliveryDto getDeliveryInfo(String accessToken) {
        Long id = jwtManager.validateJwt(accessToken);
        User user = userRepository.findById(id).orElseThrow(() -> new TempHandler(ErrorStatus.LOGIN_ERROR_ID));
        return toDeliveryDto(user);
    }

    @Override
    public DeliveryDto patchDeliveryInfo(String accessToken, DeliveryDto deliveryDto) {
        Long id = jwtManager.validateJwt(accessToken);
        User user = userRepository.findById(id).orElseThrow(() -> new TempHandler(ErrorStatus.LOGIN_ERROR_ID));
        user.setDelivery(deliveryDto);
        userRepository.save(user);
        return deliveryDto;
    }
}
