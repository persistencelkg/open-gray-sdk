package com.gray.lkg.client;

import com.gray.lkg.core.GrayDispatchManager;
import com.gray.lkg.core.GrayExpressionMatcher;
import com.gray.lkg.core.expression.JexlExpressionMatcher;
import com.gray.lkg.model.ControlEnum;
import com.gray.lkg.model.GraySwitchVo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 3:11 PM
 */
@Slf4j
public abstract class GraySwitchDispatcher {

    private final GrayCountLimiter grayCountLimiter;
    private final GrayExpressionMatcher expressionMatcher;
    private GraySwitchVo graySwitchVo;
    @Getter
    private final String switchName;

    public GraySwitchDispatcher(String switchName) {
        this.switchName = switchName;
        this.grayCountLimiter = new GrayCountLimiter(switchName);
        this.expressionMatcher = new JexlExpressionMatcher();
        GrayDispatchManager.registerAndTriggerGrayEvent(switchName, this::onGrayEvent);
    }

    private void onGrayEvent(GraySwitchVo graySwitchVo) {
        this.graySwitchVo = graySwitchVo;
        Optional.ofNullable(graySwitchVo).map(GraySwitchVo::getGrayCondition).ifPresent(expressionMatcher::setExpression);
        log.info("load gray strategy success:{}", graySwitchVo);
    }


    private boolean dispatch(Map<String, Object> args) {
        // 优先判断基于次数灰度
        GraySwitchVo.GrayTime grayCount = graySwitchVo.getGrayCount();
        if (Objects.nonNull(grayCount)) {
            return grayCountLimiter.hasPermission(args);
        }
        GraySwitchVo.GrayWeight grayWeight = graySwitchVo.getGrayWeight();
        if (Objects.isNull(grayWeight)) {
            return false;
        }
        return grayWeight.hit();
    }

    public boolean get() {
        return get(Collections.emptyMap());
    }


    public boolean get(Map<String, Object> args) {
        GraySwitchVo newGraySwitch = this.graySwitchVo;
        if (Objects.isNull(newGraySwitch)) {
            return false;
        }
        if (Objects.equals(newGraySwitch.getControlType(), ControlEnum.ALL_OLD.getType())) {
            return false;
        }
        if (Objects.equals(newGraySwitch.getControlType(), ControlEnum.ALL_NEW.getType())) {
            return true;
        }
        if (expressionMatcher.match(args)) {
            return dispatch(args);
        } else {
            log.debug("gray match fail reason: expression:{} args:{}", expressionMatcher.getExpression(), args);
        }
        return false;

    }

}
