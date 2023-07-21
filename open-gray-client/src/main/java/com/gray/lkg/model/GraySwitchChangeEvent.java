package com.gray.lkg.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: 灰度最新变更数据
 * @author: 李开广
 * @date: 2023/7/17 9:02 PM
 */
@Data
public class GraySwitchChangeEvent {

    private String switchName;

    private GraySwitchVo oldSwitch;

    private GraySwitchVo newSwitch;

    public GraySwitchChangeEvent(GraySwitchVo newSwitch) {
        this.switchName = newSwitch.getSwitchName();
        this.newSwitch = newSwitch;
    }

}
