package com.kg.server.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.kg.server.bo.ChangeConfigBo;
import com.kg.server.vo.GrayLongPollRequest;
import com.kg.server.vo.GraySwitchVo;
import com.kg.server.controller.GrayServerController;
import lombok.extern.slf4j.Slf4j;
import org.lkg.request.CommonIntResp;
import org.lkg.request.CommonResp;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
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
@EnableScheduling // todo temp mock
public class GrayLongPollService {

    // 持续保存的服务的版本信息 + 规则列表
    private static final ConcurrentHashMap<String, ChangeConfigBo> CACHE = new ConcurrentHashMap<>();

    /**
     * 一个服务多个开关 共用一个长连接，因此会出现一个改变其余都同步的情况
     */
    private static final SetMultimap<String, DeferredResult<CommonIntResp<ChangeConfigBo>>> SERVER_GRAY_CONFIG_CHANGE_RESULT = Multimaps.synchronizedSetMultimap(HashMultimap.create());


    public static void register(String serverName, DeferredResult<CommonIntResp<ChangeConfigBo>> result) {
        SERVER_GRAY_CONFIG_CHANGE_RESULT.put(serverName, result);
    }

    public static void deRegister(String serverName, DeferredResult<CommonIntResp<ChangeConfigBo>> result) {
        SERVER_GRAY_CONFIG_CHANGE_RESULT.remove(serverName, result);
    }

    public CommonIntResp<ChangeConfigBo> grayConfigChangeAware(GrayLongPollRequest request) {

        ChangeConfigBo result = null;
        try {
            // 校验版本
            checkVersion(request.getSdkVersion());
            // 检查本地缓存是否发生变更
            result = checkIfModifyConfig(request.getServerName(), request.getGrayVersion());
        } catch (Exception e) {
            log.warn("servername:{} check gray config change err:{}", request.getServerName(), e.getMessage(), e);
        }
        return CommonResp.successInt(result);
    }

    private ChangeConfigBo checkIfModifyConfig(String serverName, Long oldVersion) {
        ChangeConfigBo changeConfigBo = CACHE.get(serverName);
        if (Objects.isNull(changeConfigBo)) {
            return null;
        }
        List<GraySwitchVo> graySwitchVoList = changeConfigBo.getGraySwitchVoList();
        if (ObjectUtils.isEmpty(graySwitchVoList)) {
            return null;
        }
        if (Objects.isNull(oldVersion) || changeConfigBo.getGrayVersion() >= oldVersion) {
            return changeConfigBo;
        }
        return null;
    }

    private void checkVersion(String grayVersion) {
        // throw or return null
    }


    @Scheduled(cron = "0/5 * * * * ?")
    public void mockChange() {
        GraySwitchVo graySwitchVo = GrayServerController.mockV1();
        String serverName = "open-gray";
        graySwitchVo.setGrayCount(new GraySwitchVo.GrayTime((int) (Math.random() * 3), 10));
        CACHE.put(serverName, new ChangeConfigBo(graySwitchVo.getSwitchName(), 100, Lists.newArrayList(graySwitchVo)));

        // dispatch
        ChangeConfigBo changeConfigBo = CACHE.get(serverName);
        Set<DeferredResult<CommonIntResp<ChangeConfigBo>>> deferredResults = SERVER_GRAY_CONFIG_CHANGE_RESULT.get(serverName);
        for (DeferredResult<CommonIntResp<ChangeConfigBo>> deferredResult : deferredResults) {
            if (Objects.isNull(changeConfigBo)) {
                deferredResult.setResult(CommonIntResp.successInt(new ChangeConfigBo()));
            } else {
                deferredResult.setResult(CommonIntResp.successInt(changeConfigBo));
            }
            log.info(">>>>> change mock schedule trigger");

        }
    }

    public void refresh() {
        GraySwitchVo graySwitchVo = GrayServerController.mockV1();
        graySwitchVo.setGrayCount(new GraySwitchVo.GrayTime((int) (Math.random() * 3), 10));
        long version = (long) (Math.random() * 1000L);
        CACHE.put("open-gray", new ChangeConfigBo(graySwitchVo.getSwitchName(), version, Lists.newArrayList(graySwitchVo)));

    }
}
