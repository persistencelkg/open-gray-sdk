package com.gray.lkg.core;

import com.gray.lkg.model.GraySwitchChangeEvent;
import com.gray.lkg.model.GraySwitchVo;

import java.util.List;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/17 9:23 PM
 */
public interface GraySwitchCache {

    GraySwitchVo loadSwitchByName(String switchName);

    List<GraySwitchVo> loadAllSwitches();

    void onUpdateSwitch(GraySwitchChangeEvent graySwitchChangeEvent);

}
