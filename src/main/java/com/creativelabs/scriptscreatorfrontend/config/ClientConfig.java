package com.creativelabs.scriptscreatorfrontend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ClientConfig {

    @Value("${back.api.address}")
    private String backApiAddress;
}
