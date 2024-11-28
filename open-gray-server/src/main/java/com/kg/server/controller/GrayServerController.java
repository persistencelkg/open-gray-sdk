package com.kg.server.controller;

import com.google.common.collect.Lists;
import com.kg.server.vo.GrayLongPollRequest;
import com.kg.server.vo.GraySwitchVo;
import com.kg.server.service.GrayLongPollService;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.DynamicConfigManger;
import org.lkg.request.CommonIntResp;
import org.lkg.request.DefaultResp;
import org.lkg.simple.DateTimeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    private static final DefaultResp NOT_MODIFIED_RESPONSE = DefaultResp.success(null, String.valueOf(HttpStatus.NOT_MODIFIED.value()));

    @PostMapping("/rule-list")
    public CommonIntResp<List<GraySwitchVo>> ruleList() {
        List<GraySwitchVo> list = new ArrayList<>();
        list.add(mockV1());
        return CommonIntResp.successInt(list);
    }

    @PostMapping("/long-poll")
    public DeferredResult<DefaultResp> longPool(@RequestBody @Valid GrayLongPollRequest grayLongPollRequest) {
        DeferredResult<DefaultResp> defaultRespDeferredResult = new DeferredResult<>(
                DynamicConfigManger.getLong("gray-server.delay.timeout", 59500L),
                NOT_MODIFIED_RESPONSE);
        try {
            DefaultResp defaultResp = grayLongPollService.grayConfigChangeAware(grayLongPollRequest);
            defaultRespDeferredResult.setResult(defaultResp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 到了长轮询限制的时间
            defaultRespDeferredResult.onTimeout(() -> {
                log.info("[{}] long poll timeout arrive not found:{} config change", DateTimeUtils.getCurrentTime(), grayLongPollRequest.getServerName());
            });
            // 有数据到达变化，更新结果
            defaultRespDeferredResult.onCompletion(() -> {
                log.info("[{}] detect config change", grayLongPollRequest.getServerName());
                GrayLongPollService.deRegister(grayLongPollRequest.getServerName(), defaultRespDeferredResult);
            });
            GrayLongPollService.registerAndReturn(grayLongPollRequest.getServerName(), defaultRespDeferredResult);
        }
        // 注册同一个服务多个不同的规则
        return defaultRespDeferredResult;
    }


    public static GraySwitchVo mockV1() {
        GraySwitchVo graySwitchVo = new GraySwitchVo();
        graySwitchVo.setSwitchName("hit-gray");
        graySwitchVo.setGrayCondition("(1==1)");
        graySwitchVo.setGrayType(0);
        graySwitchVo.setInstanceList(Lists.newArrayList("open-gray"));
        graySwitchVo.setGrayCount(new GraySwitchVo.GrayTime(10, 60));
        graySwitchVo.setControlType(0);
        graySwitchVo.setStatus(1);
        graySwitchVo.setOriginConditionList(Lists.newArrayList(new GraySwitchVo.GrayRuleExpression("1", "==", "1", "", 1)));
        return graySwitchVo;
    }
}
