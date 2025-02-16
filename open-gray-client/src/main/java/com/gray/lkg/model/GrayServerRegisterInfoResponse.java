package com.gray.lkg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
/**
 * @author likaiguang
 * @date 2025/1/20
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GrayServerRegisterInfoResponse {

    public static final int ENABLED = 0;

    public static final int DISABLED = 1;

    private String serverName;

    private String grayServerName;

    private int status;

    private int controlType;

    private String ip;

    private int port;
}
