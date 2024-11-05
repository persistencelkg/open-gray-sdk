package com.gray.lkg.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/1 9:56 PM
 */
@Data
@AllArgsConstructor
public class GrayEvent {

    private String key;
    private GraySwitchVo oldSwitch;
    private GraySwitchVo newSwitch;

    public GrayEvent(GraySwitchVo graySwitchVo) {}
}

