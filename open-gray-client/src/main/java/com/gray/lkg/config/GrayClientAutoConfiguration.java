package com.gray.lkg.config;

import com.gray.lkg.intercepter.filter.GrayFilterInterceptor;
import com.gray.lkg.intercepter.filter.CommonFilterInterceptor;
import com.gray.lkg.model.GrayServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 灰度客户端的配置类
 * @author: 李开广
 * @date: 2023/7/14 4:18 PM
 */
@Configuration
@EnableConfigurationProperties(GrayServerProperties.class)
@ConditionalOnProperty(name = "global.gray.switch",  matchIfMissing = false)
public class GrayClientAutoConfiguration {


    // 过滤器拦截器 - 
    @Bean
    public CommonFilterInterceptor grayFilterInterceptor() {
        return new GrayFilterInterceptor();
    }





}
