package com.gray.lkg.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/12/9 9:03 PM
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LongPollData {

    private long grayVersion;

    private List<GraySwitchVo> graySwitchVoList;
}
