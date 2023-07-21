package com.gray.lkg.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 灰度状态枚举
 * @author: 李开广
 * @date: 2023/7/17 6:52 PM
 */
@Getter
@AllArgsConstructor
public enum GrayStatusEnum {

    /**
     * 灰度控制
     */
    GRAY_CONTROL(0),
    /**
     * 全走新
     */
    ALL_IN_NEW(1),
    ALL_IN_OLD(2);

    private final Integer code;
}
