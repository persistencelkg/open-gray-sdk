package com.gray.lkg.client;

import com.gray.lkg.core.GrayDispatchManager;
import com.gray.lkg.model.GrayRuleExpression;
import com.gray.lkg.model.GraySwitchVo;
import org.lkg.enums.StringEnum;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 2:18 PM
 */
public class GrayCountLimiter {

    /**
     * 通用限流器
     */
    private final LongAdder commonCount = new LongAdder();
    /**
     * 基于参数控制限流器
     */
    private final Map<String, LongAdder> paramCountMap = new HashMap<>();

    // ----------------------- core param -------------------
    private long lastUpdatedTime = System.currentTimeMillis();
    private int grayCircleCount;
    private long grayCircleSecond;


    protected GrayCountLimiter(String switchName) {
        GrayDispatchManager.registerAndTriggerGrayEvent(switchName, this::onGrayEvent);
    }

    protected boolean hasPermission(Map<String, Object> args) {
        if (ObjectUtil.isEmpty(paramCountMap) || ObjectUtil.isEmpty(args)) {
            return limit(commonCount);
        }
        // by param limit
        for (Map.Entry<String, Object> entry : args.entrySet()) {
            if (ObjectUtil.isEmpty(entry.getValue())) {
                continue;
            }
            String paramKey = getParamKey(entry.getKey(), entry.getValue().toString());
            if (paramCountMap.containsKey(paramKey)) {
                return limit(paramCountMap.get(paramKey));
            }
        }
        return false;

    }

    private boolean limit(LongAdder longAdder) {
        checkPeriod();
        longAdder.increment();
        if (longAdder.sum() <= grayCircleCount) {
            return true;
        }
        return false;
    }

    private void checkPeriod() {
        long now = System.currentTimeMillis();
        if (now - lastUpdatedTime <= grayCircleSecond) {
            return;
        }
        commonCount.reset();
        HashMap<String, LongAdder> map = new HashMap<>(paramCountMap);
        map.forEach((k, v) -> v.reset());
        lastUpdatedTime = now;
    }


    private void onGrayEvent(GraySwitchVo graySwitchVo) {
        if (Objects.isNull(graySwitchVo) || Objects.isNull(graySwitchVo.getGrayCount())) {
            paramCountMap.clear();
            return;
        }
        GraySwitchVo.GrayTime grayCount = graySwitchVo.getGrayCount();
        this.grayCircleCount = grayCount.getGrayCount();
        this.grayCircleSecond = grayCount.getGrayPeriod() * 1000;

        List<GrayRuleExpression> originConditionList = graySwitchVo.getOriginConditionList();
        HashMap<String, LongAdder> back = new HashMap<>(paramCountMap);
        for (GrayRuleExpression grayRuleExpression : originConditionList) {
            if (!grayRuleExpression.isSpecial() || !Objects.equals(grayRuleExpression.getOperator(), GrayRuleExpression.Operator.in)) {
                continue;
            }
            // each value each gray strategy
            String params = grayRuleExpression.getParams();
            List<String> valueList = JacksonUtil.readList(grayRuleExpression.getValue(), String.class);
            if (ObjectUtil.isNotEmpty(valueList)) {
                valueList.forEach(val -> {
                    String key = getParamKey(params, val);
                    paramCountMap.putIfAbsent(key, new LongAdder());
                    back.remove(key);
                });
            }
        }
        // 将不存在的key抛弃
        back.forEach(paramCountMap::remove);
    }

    public static String getParamKey(String key, String value) {
        return String.join(StringEnum.COLON, key, value);
    }
}
