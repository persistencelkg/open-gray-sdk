package com.gray.lkg.model;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/1 8:48 PM
 */

@Data
public class GraySwitchResponse {

    private int code;
    private String message;
    private List<GraySwitchVo> data;
}
