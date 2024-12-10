package com.kg.server.vo;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/22 4:18 PM
 */
@Data
public class GrayLongPollRequest {

    @NotBlank(message = "server name can not empty")
    private String serverName;

    /**
     * 升级版本的校验，考虑到强升级版本的需要
     */
//    @NotNull(message = "gray version can not empty")
    private Long grayVersion;

    private String sdkVersion;


//    @Valid
    private List<ServerSwitchVersion> grayVersionList;



    @Data
    public static class ServerSwitchVersion{
        @NotBlank(message = "gray switch name can not empty")
        private String graySwitchName;
        @NotBlank(message = "switch version can not empty")
        private Long switchVersion;
    }
}
