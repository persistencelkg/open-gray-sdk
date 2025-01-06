package com.kg.server;

import org.lkg.apollo.EnableDynamicApollo;
import org.lkg.config.DynamicConfigOption;
import org.lkg.config.EnableOpenArhatOptionConfig;
import org.lkg.metric.sql.mybatis.MybatisMonitorAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 8:03 PM
 */
@SpringBootApplication
@EnableOpenArhatOptionConfig(type = {
        DynamicConfigOption.DYNAMIC_CONFIG_OPTION,
        DynamicConfigOption.METRIC_CONFIG,
        DynamicConfigOption.TTL_CONFIG})
public class GrayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrayServerApplication.class, args);
    }
}
