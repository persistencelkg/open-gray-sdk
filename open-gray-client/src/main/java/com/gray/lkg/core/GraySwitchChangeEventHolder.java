package com.gray.lkg.core;

import com.gray.lkg.model.GraySwitchChangeEvent;
import com.gray.lkg.model.GraySwitchVo;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/17 8:54 PM
 */
public class GraySwitchChangeEventHolder {



    private final Map<String, GraySwitchVo> map = new ConcurrentHashMap<>();



    public void onGraySwitchChange(GraySwitchChangeEvent graySwitchChangeEvent) {
        GraySwitchVo oldSwitch = map.remove(graySwitchChangeEvent.getSwitchName());
        GraySwitchVo newSwitch = graySwitchChangeEvent.getNewSwitch();
        if (Objects.isNull(newSwitch)) {

        }
    }

}
