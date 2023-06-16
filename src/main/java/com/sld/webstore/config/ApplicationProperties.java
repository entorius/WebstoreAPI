package com.sld.webstore.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private String clientOriginUrl;

    private String audience;
}
