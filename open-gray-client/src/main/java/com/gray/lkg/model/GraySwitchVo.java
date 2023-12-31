package com.gray.lkg.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @description: 灰度的开关规则数据来自gray-server
 * @author: 李开广
 * @date: 2023/7/17 5:25 PM
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GraySwitchVo {

    private String switchName;

    private String serverName;

    private String ip;

    private String oldDownStream;

    private String oldUrl;

    private String newDownStream;

    private String newUrl;

    /**
     * 灰度类型 0 业务灰度
     */
    private Integer grayType;

    /**
     * 灰度条件表达式
     */
    private String grayCondition;

    private Integer languageType;

    private Integer status;

    private Integer globalStatus;

    /**
     * 按权重
     */
    private List<GrayWeight> grayWeightList;

    /**
     * 按次数
     */
    private List<GrayTime> grayTimeList;



    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private static class GrayWeight {
        /**
         * 多少流量走老逻辑
         */
        private double defaultWeight;
        /**
         * 多少流量走新逻辑
         */
        private double grayWeight;

        public boolean hit() {
            double random = ThreadLocalRandom.current().nextDouble(0, grayWeight + defaultWeight);
            boolean res;
            if (defaultWeight < grayWeight) {
                res = random >= defaultWeight;
            } else {
                res = random < grayWeight;
            }
            return res;
        }

    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private static class GrayTime{

        private Integer grayCount;

        private Integer grayPeriod;
    }
}
