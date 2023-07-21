package com.gray.lkg.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @description: 灰度平台的配置信息
 * @author: 李开广
 * @date: 2023/7/14 4:19 PM
 */
@Data
@ConfigurationProperties(prefix = "gray-server")
public class GrayServerProperties {

    // 公司内部服务
    private static final String HTTP_PROTOCOL = "http://";

    private String serverName;

    private List<String> hosts;

    private String grayStrategiesUri;

    private String grayChangeStrategyUri;

    public String getPollGrayStrategyUrL() {
        return HTTP_PROTOCOL + serverName + grayStrategiesUri;
    }


    public String getPollGrayChangeStrategyUrL() {
        return HTTP_PROTOCOL + serverName + grayChangeStrategyUri;
    }
}
