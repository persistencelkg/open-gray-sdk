package com.gray.lkg.model;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/12/9 9:03 PM
 */
@Data
public class LongPollData {

    private long grayVersion;

    private List<GraySwitchVo> ruleList;
}
