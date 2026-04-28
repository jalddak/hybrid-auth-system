package com.has.backend.common.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cookie")
@Getter
@Setter
public class CookieProperties {
    private int deleteAge;
    private int maxAge;
}

