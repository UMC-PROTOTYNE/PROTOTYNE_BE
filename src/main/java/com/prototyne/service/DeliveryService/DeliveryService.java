package com.prototyne.service.DeliveryService;

import com.prototyne.web.dto.DeliveryDto;

public interface DeliveryService {
    DeliveryDto getDeliveryInfo(String accessToken);

    DeliveryDto patchDeliveryInfo(String accessToken, DeliveryDto deliveryDto);
}
