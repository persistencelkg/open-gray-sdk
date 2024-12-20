package com.gray.lkg.core;

import com.gray.lkg.model.GraySwitchVo;
import com.gray.lkg.model.GrayTypeEnum;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/4 3:35 PM
 */
public interface GraySwitchService {

    List<GraySwitchVo> listAllGraySwitch(GrayTypeEnum typeEnum);

    default List<GraySwitchVo> listAllGraySwitch() {
        return listAllGraySwitch(null);
    }

    GraySwitchVo getBySwitchName(String switchName);
}
