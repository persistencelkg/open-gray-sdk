package com.gray.lkg.core.flow;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.config.impl.ConfigHttpClientManager;
import com.alibaba.nacos.common.constant.HttpHeaderConsts;
import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.NacosRestTemplate;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.http.param.MediaType;
import com.alibaba.nacos.common.http.param.Query;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gray.lkg.model.GrayServerRegisterInfoResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lkg.enums.StringEnum;
import org.lkg.exception.IResponseEnum;
import org.lkg.request.CommonResp;
import org.lkg.request.DefaultResp;
import org.lkg.utils.JacksonUtil;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2025/2/10 7:35 PM
 */
@Slf4j
public class FlowGrayInfoLoader {

    @Getter
    private final NamingService namingService;

    @Getter
    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    @Value("${open.gray.server.name:open-gray-server}")
    private String grayServerName;

    @Value("${open.gray.register-info-uri:}")
    private String openGrayRegisterInfoUri;


    @Value("${gray.nacos.address:}")
    private String nacosAddress;

    // api: https://nacos.io/zh-cn/docs/open-api.html  since 2.2.0 use 2.x API

    @Value("${gray.nacos.login.url:/nacos/v1/auth/users/login}")
    public String loginUrl;

    @Value("${gray.nacos.operate.url:/nacos/v1/ns/instance}")
    public String nacosOperateUrl;


    public FlowGrayInfoLoader(NamingService namingService, NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.namingService = namingService;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    }

    private String getUri(String uri) {
        return nacosAddress.startsWith(StringEnum.HTTP_PREFIX) ? nacosAddress : StringEnum.HTTP_PREFIX + nacosAddress
                + (uri.startsWith("/") ? uri : "/" + uri);
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
            if (Objects.isNull(result) || Objects.isNull(result.getData())) {
                return Collections.emptyList();
            }
            return result.getData();
        } catch (Exception e) {
            log.error("load gray server info failed", e);
            return Collections.emptyList();
        }
    }

    public void updateInstance(String serverName, String ip, Integer port, Boolean enableStatus) {
        Map<String, String> params = new HashMap<>();
        params.put("enabled", String.valueOf(enableStatus));
        if (!enableStatus) {
            params.put("healthy", "false");
            params.put("weight", "0.0");
        }
        restReqWithOutBeat(serverName, ip, port, params, false);

    }

    public void registerWithOutBeatAndNoWeight(String serverName, String ip, Integer port, Boolean isGraying) {
        Map<String, String> params = new HashMap<>();
        params.put("enabled", String.valueOf(!isGraying));
        if (isGraying) {
            params.put("healthy", "false");
            params.put("weight", "0.0");
        }
        restReqWithOutBeat(serverName, ip, port, params, true);
        log.info("common server:{} registered, enable:{}", serverName, !isGraying);
    }


    private void restReqWithOutBeat(String serverName,
                                    String ip,
                                    Integer port,
                                    Map<String, String> params, boolean insert) {
        String accessToken = getAccessToken(nacosDiscoveryProperties.getUsername(), nacosDiscoveryProperties.getPassword());
        if (StringUtils.isEmpty(accessToken)) {
            throw new RuntimeException("nacos update instance fail, reason: not permission");
        }
        params.put("serviceName", serverName);
        params.put("groupName", Constants.DEFAULT_GROUP);
        params.put("ip", ip);
        params.put("port", String.valueOf(port));
        params.put("namespaceId", nacosDiscoveryProperties.getNamespace());
        params.put("accessToken", accessToken);
        if (log.isDebugEnabled()) {
            log.debug("nacos operate param：{}", params);
        }
        try {
            String url = "http://" + nacosAddress + nacosOperateUrl;
            Query query = Query.newInstance().initParams(params);
            Header header = Header.newInstance().addParam(HttpHeaderConsts.CONTENT_TYPE, MediaType.TEXT_PLAIN);
            HttpRestResult<String> result;
            if (insert) {
                result = nacosRestTemplate.post(url, header, query, null, String.class);
            } else {
                result = nacosRestTemplate.put(url, header, query, null, String.class);
            }
            log.info("nacos operate result：{}", result);
        } catch (Exception e) {
            log.error("nacos operate fail, ：{},{}", e.getCause(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getAccessToken(String name, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("username", name);
        params.put("password", password);
        try {
            String url = "http://" + nacosAddress + loginUrl;
            Query query = Query.newInstance().initParams(params);
            HttpRestResult<DefaultResp>
                    result = nacosRestTemplate.post(url, null, query,
                    null,
                    DefaultResp.class);
            if (Objects.isNull(result) || Objects.isNull(result.getData())) {
                return null;
            }
            return JacksonUtil.objToMap(result.getData()).get("accessToken").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
