package com.prototyne.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;

@Configuration
public class SwaggerBeanConfig {

    public SwaggerBeanConfig(MappingJackson2HttpMessageConverter converter) {
        // 스웨거에서 Content-Type에 ctetstream 허용
        var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
    }
}