package com.kg.server.controller;

import com.google.common.collect.Lists;
import com.kg.server.bo.ChangeConfigBo;
import com.kg.server.vo.GrayLongPollRequest;
import com.kg.server.vo.GraySwitchVo;
import com.kg.server.service.GrayLongPollService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.DynamicConfigManger;
import org.lkg.request.CommonIntResp;
import org.lkg.simple.DateTimeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 8:05 PM
 */
@RestController
@RequestMapping("/gray")
@Slf4j
public class GrayServerController {

    @Resource
    private GrayLongPollService grayLongPollService;

    private static final CommonIntResp<ChangeConfigBo> NOT_MODIFIED_RESPONSE = CommonIntResp.successInt(null, HttpStatus.NOT_MODIFIED.value());

    @PostMapping("/rule-list")
    public CommonIntResp<List<GraySwitchVo>> ruleList() {
        List<GraySwitchVo> list = new ArrayList<>();
        list.add(mockV1());
        list.add(mockV2());
        return CommonIntResp.successInt(list);
    }

    @GetMapping("/refesh")
    public boolean refresh() {
        grayLongPollService.refresh();
        return true;
    }

    @PostMapping("/long-poll")
    public DeferredResult<CommonIntResp<ChangeConfigBo>> longPool(@RequestBody @Valid GrayLongPollRequest grayLongPollRequest) {
        log.info("request：{}", grayLongPollRequest);
        DeferredResult<CommonIntResp<ChangeConfigBo>> defaultRespDeferredResult = new DeferredResult<>(
                DynamicConfigManger.getLong("gray-server.delay.timeout", 29500L),
                NOT_MODIFIED_RESPONSE);
        try {
            // 先判断状态，再去考虑获取数据
            // 1.服务不存在： 无论是真的不存在， 还是其开关都下线了  都交给异步通知去处理，而不是这里立刻返回，这样会持续建立无用链接
            if (grayLongPollService.checkServer(grayLongPollRequest)) {
                CommonIntResp<ChangeConfigBo> defaultResp = grayLongPollService.grayConfigChangeAware(grayLongPollRequest);
                if (Objects.nonNull(grayLongPollRequest.getGrayVersion())) {
                    defaultRespDeferredResult.setResult(defaultResp);
                    return defaultRespDeferredResult;
                } else if (Objects.nonNull(defaultResp.getData())) {
                    defaultRespDeferredResult.setResult(defaultResp);
                    return defaultRespDeferredResult;
                } else {
                    // 初始化时，没数据，交给异步通知
                }
            } else {
                // 服务开关下线 考虑后面请求都需要
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        dealWithTimeout(grayLongPollRequest, defaultRespDeferredResult);
        // 注册同一个服务多个不同的规则
        return defaultRespDeferredResult;
    }

    private static void dealWithTimeout(GrayLongPollRequest grayLongPollRequest, DeferredResult<CommonIntResp<ChangeConfigBo>> defaultRespDeferredResult) {
        // 到了长轮询限制的时间
        defaultRespDeferredResult.onTimeout(() -> {
            log.info("[{}] long poll timeout arrive not found:{} config change", DateTimeUtils.getCurrentTime(), grayLongPollRequest.getServerName());
        });
        // 有数据到达变化，更新结果
        defaultRespDeferredResult.onCompletion(() -> {
            log.info("[{}] detect config change", grayLongPollRequest.getServerName());
            GrayLongPollService.deRegister(grayLongPollRequest.getServerName(), defaultRespDeferredResult);
        });
        GrayLongPollService.register(grayLongPollRequest.getServerName(), defaultRespDeferredResult);
        log.info("register server:{}", grayLongPollRequest.getServerName());
    }


    public static GraySwitchVo mockV1() {
        GraySwitchVo graySwitchVo = new GraySwitchVo();
        graySwitchVo.setSwitchName("hit-gray");
        graySwitchVo.setServerName("open-gray");
        graySwitchVo.setGrayCondition("(1==1)");
        graySwitchVo.setGrayType(0);
        graySwitchVo.setInstanceList(Lists.newArrayList("open-gray", "open-gray-0"));
        graySwitchVo.setGrayCount(new GraySwitchVo.GrayTime(10, 60));
        graySwitchVo.setControlType(0);
        graySwitchVo.setStatus(1);
        graySwitchVo.setOriginConditionList(Lists.newArrayList(new GraySwitchVo.GrayRuleExpression("1", "==", "1", "", 1)));
        return graySwitchVo;
    }


    public static GraySwitchVo mockV2() {
        // 流量灰度
        GraySwitchVo graySwitchVo = new GraySwitchVo();
        graySwitchVo.setSwitchName("flow-gray-test");
        graySwitchVo.setServerName("open-gray");
        graySwitchVo.setGrayCondition("(1==1)");
        graySwitchVo.setGrayType(1);
        graySwitchVo.setOldDownStream("open-gray-server");
        graySwitchVo.setOldUri("/feign/old-uri");
        graySwitchVo.setNewDownStream("open-gray-server");
        graySwitchVo.setNewUri("/feign/new-uri");
        graySwitchVo.setGrayWeight(new GraySwitchVo.GrayWeight(10, 90));
        graySwitchVo.setInstanceList(Lists.newArrayList("open-gray", "open-gray-0"));
//        graySwitchVo.setGrayCount(new GraySwitchVo.GrayTime(10, 60));
        graySwitchVo.setControlType(0);
        graySwitchVo.setStatus(1);
//        graySwitchVo.setOriginConditionList(Lists.newArrayList(new GraySwitchVo.GrayRuleExpression("1", "==", "1", "", 1)));
        return graySwitchVo;
    }
}
