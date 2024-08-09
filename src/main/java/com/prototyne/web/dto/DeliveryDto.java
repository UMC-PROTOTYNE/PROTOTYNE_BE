package com.prototyne.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
    private String deliveryName;
    private String deliveryPhone;
    private String deliveryAddress;
}
