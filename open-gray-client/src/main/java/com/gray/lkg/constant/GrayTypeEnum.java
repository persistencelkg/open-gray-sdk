package com.gray.lkg.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/17 6:35 PM
 */
@Getter
@AllArgsConstructor
public enum GrayTypeEnum {
    /**
     * 业务灰度
     */
    BIZ_GRAY(0),
    FLOW_GRAY(1),
    ;

    private final Integer code;


}
