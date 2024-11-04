package com.gray.lkg.config;

import io.github.persistence.LongPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 灰度客户端的配置类
 * @author: 李开广
 * @date: 2023/7/14 4:18 PM
 */
@Configuration
@ConditionalOnProperty(name = "global.gray.switch",  matchIfMissing = false)
public class GrayClientAutoConfiguration {

    @ConfigurationProperties(prefix = "gray.long-pool")
    @Bean
    public LongPoolConfig grayLongPoolConfig() {
        return new LongPoolConfig();
    }

}
