package com.gray.lkg.core.flow;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.config.impl.ConfigHttpClientManager;
import com.alibaba.nacos.common.constant.HttpHeaderConsts;
import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.NacosRestTemplate;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.http.param.MediaType;
import com.alibaba.nacos.common.http.param.Query;
import com.gray.lkg.model.GrayServerRegisterInfoResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.SameLen;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2025/2/10 7:35 PM
 */
@Slf4j
public class FlowGrayInfoLoader {

    @Getter
    private final NamingService namingService;

    @Value("${open.gray.server.name:open-gray-server}")
    private String grayServerName;

    @Value("${open.gray.register-info-uri:}")
    private String openGrayRegisterInfoUri;

    public FlowGrayInfoLoader(NamingService namingService) {
        this.namingService = namingService;
    }

    private final NacosRestTemplate nacosRestTemplate = ConfigHttpClientManager.getInstance()
            .getNacosRestTemplate();

    public List<GrayServerRegisterInfoResponse> loadGrayList(String serviceName, String ip, Integer port) {
        Map<String, Object> params = new HashMap<>();
        params.put("server_name", serviceName);
        params.put("ip", ip);
        params.put("port", port);
        Map<String, Object> body = new HashMap<>();
        body.put("params", params);
        try {
            Instance instance = namingService.selectOneHealthyInstance(grayServerName);
            String url = "http://" + instance.getIp() + ":" + instance.getPort() + openGrayRegisterInfoUri;
            Header header = Header.newInstance().addParam(HttpHeaderConsts.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            HttpRestResult<List<GrayServerRegisterInfoResponse>>
                    result = nacosRestTemplate.post(url, header, Query.EMPTY,
                    body, List.class);
            return result == null ? Collections.emptyList() : result.getData();
        } catch (Exception e) {
            log.error("load gray server info failed", e);
            return Collections.emptyList();
        }
    }


}
