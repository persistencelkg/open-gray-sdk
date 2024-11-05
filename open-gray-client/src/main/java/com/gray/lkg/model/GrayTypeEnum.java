package com.gray.lkg.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/5 8:25 PM
 */
@Getter
@AllArgsConstructor
public enum GrayTypeEnum {

    BUSINESS_GRAY(0),

    FLOW_GRAY(1)

    ;

    private final int code;
}
