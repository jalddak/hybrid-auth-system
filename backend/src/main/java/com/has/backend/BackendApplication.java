package com.has.backend;

import com.has.backend.common.config.property.CookieProperties;
import com.has.backend.common.config.property.CorsConfigProperties;
import com.has.backend.common.config.property.EmailProperties;
import com.has.backend.common.config.property.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableConfigurationProperties({
        CorsConfigProperties.class, JwtProperties.class,
        EmailProperties.class, CookieProperties.class
})
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
