package com.kg.server.controller;

import com.google.common.collect.Lists;
import com.kg.server.bo.GraySwitchVo;
import org.lkg.request.CommonIntResp;
import org.lkg.request.CommonResp;
import org.lkg.simple.ServerInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class GrayServerController {

    @PostMapping("/rule-list")
    public CommonIntResp<List<GraySwitchVo>> ruleList() {
        List<GraySwitchVo> list = new ArrayList<>();
        list.add(mockV1());
        return CommonIntResp.successInt(list);
    }

    public GraySwitchVo mockV1() {
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
