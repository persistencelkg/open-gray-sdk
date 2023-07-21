package com.gray.lkg.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 流量灰度的自动配置类
 * @author: 李开广
 * @date: 2023/7/17 7:52 PM
 */
@Configuration
@ConditionalOnProperty(name = "global.gray.switch",  matchIfMissing = false)
public class FlowGrayAutoConfiguration {

    

}
