package com.gray.lkg.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/4 8:05 PM
 */
@AllArgsConstructor
@Getter
public enum ControlEnum {

    GRAY(0),
    ALL_NEW(1),
    ALL_OLD(2);
    private final int type;
}
