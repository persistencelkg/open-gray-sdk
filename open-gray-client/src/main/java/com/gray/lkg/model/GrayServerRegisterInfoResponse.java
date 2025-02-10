package com.gray.lkg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
/**
 * @author likaiguang
 * @date 2025/1/20
 */
@Data
public class GrayServerRegisterInfoResponse {

    public static final int ENABLED = 0;

    public static final int DISABLED = 1;

    private String serverName;

    private String grayServerName;

    @JsonProperty("global_status")
    @JsonAlias("enabled")
    private int enabled;

    @JsonProperty("route_rule_enabled")
    @JsonAlias("grayEnabled")
    private int grayEnabled;

    private String ip;

    private int port;
}
