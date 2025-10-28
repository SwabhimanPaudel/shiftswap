package com.swabhiman.shiftswap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // This annotation turns on the automatic timestamps
public class JpaConfig {
    // @CreatedDate and @LastModifiedDate will now work automatically
}