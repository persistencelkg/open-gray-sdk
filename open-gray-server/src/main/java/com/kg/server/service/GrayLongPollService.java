package com.kg.server.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.kg.server.vo.GrayLongPollRequest;
import com.kg.server.vo.GraySwitchVo;
import com.kg.server.controller.GrayServerController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiMap;
import org.lkg.request.DefaultResp;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/22 4:36 PM
 */
@Service
@Slf4j
@EnableScheduling // temp mock
public class GrayLongPollService {

    private static final ConcurrentHashMap<String, Map<String, GraySwitchVo>> CACHE = new ConcurrentHashMap<>();

    private static final SetMultimap<String, DeferredResult<DefaultResp>> SERVER_GRAY_CONFIG_CHANGE_RESULT = Multimaps.synchronizedSetMultimap(HashMultimap.create());


    public static DeferredResult<DefaultResp> registerAndReturn(String serverName, DeferredResult<DefaultResp> result) {
        SERVER_GRAY_CONFIG_CHANGE_RESULT.put(serverName, result);
        return result;
    }

    public static void deRegister(String serverName, DeferredResult<DefaultResp> result) {
        SERVER_GRAY_CONFIG_CHANGE_RESULT.remove(serverName, result);
    }

    public DefaultResp grayConfigChangeAware(GrayLongPollRequest request) {

        Object result = null;
        try {
            // 校验版本
            checkVersion(request.getGrayVersion());
            // 检查本地缓存是否发生变更
            result = checkIfModifyConfig(request.getServerName(), request.getGrayVersionList());
        } catch (Exception e) {
            log.warn("servername:{} check gray config change err:{}", request.getServerName(), e.getMessage(), e);
        }
        return DefaultResp.success(result);
    }

    private Object checkIfModifyConfig(String serverName, List<GrayLongPollRequest.ServerSwitchVersion> switchVersionList) {
        Map<String, GraySwitchVo> stringGraySwitchVoMap = CACHE.get(serverName);
        if (Objects.isNull(stringGraySwitchVoMap)) {
            return null;
        }
        Set<String> serverGrayConfigKeySet = stringGraySwitchVoMap.keySet();
        for (GrayLongPollRequest.ServerSwitchVersion serverSwitchVersion : switchVersionList) {
            if (serverGrayConfigKeySet.contains(serverSwitchVersion.getGraySwitchName())
                    && !Objects.equals(stringGraySwitchVoMap.get(serverSwitchVersion.getGraySwitchName()).getVersion(), serverSwitchVersion.getGrayVersion())) {
                return CACHE.values();
            }
        }
        return null;
    }

    private void checkVersion(String grayVersion) {
        // throw or return null
    }


    @Scheduled(cron = "0/3 * * * * ?")
    public void mockChange() {
        GraySwitchVo graySwitchVo = GrayServerController.mockV1();
        HashMap<String, GraySwitchVo> map = new HashMap<>();
        graySwitchVo.setGrayCount(new GraySwitchVo.GrayTime((int) (Math.random() * 3), 10));
        map.put(graySwitchVo.getSwitchName(), graySwitchVo);
        CACHE.put("open-gray-server", map);
        log.info(">>>>> change mock schedule");
        // dispatch
        Map<String, GraySwitchVo> stringGraySwitchVoMap = CACHE.get("open-gray-server");
        Collection<GraySwitchVo> values = stringGraySwitchVoMap.values();
        Set<DeferredResult<DefaultResp>> deferredResults = SERVER_GRAY_CONFIG_CHANGE_RESULT.get("open-gray-server");
        for (DeferredResult<DefaultResp> deferredResult : deferredResults) {
            deferredResult.setResult(DefaultResp.success(values));
        }
    }
}
