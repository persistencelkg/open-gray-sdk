package com.kg.server.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 8:06 PM
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GraySwitchVo {

    private String switchName;

    private String serverName;

    private Long version;

    /**
     * 灰度类型 0 业务灰度  1 流量灰度
     */
    private Integer grayType;


    // ---------------- 灰度核心参数 ----------------

    private List<String> instanceList;
    private Integer chooseAll;

    private String oldDownStream;

    private String oldUri;

    private String newDownStream;

    private String newUri;

    /**
     * 灰度条件表达式
     */
    private String grayCondition;

    private List<GrayRuleExpression> originConditionList;

    /**
     * 0 JAVA  1 GO  2 Python
     */
    private Integer languageType;

    /**
     * 按权重
     */
    private GrayWeight grayWeight;

    /**
     * 按次数
     */
    private GrayTime grayCount;

    /**
     * 当controlType = 0 是 status才有意义
     * 0 关闭
     * 1 开启
     */
    private Integer status;

    /**
     * 开关控制类型： 0 按灰度控制 1 全走新流量 2 全走老流量
     */
    private Integer controlType;


    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @AllArgsConstructor
    public static class GrayWeight {
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
    @AllArgsConstructor
    public static class GrayTime {

        /**
         * 多少次
         */
        private Integer grayCount;

        /**
         * 固定基于秒
         */
        private Integer grayPeriod;
    }


    @Data
    @AllArgsConstructor
    public static class GrayRuleExpression {

        private String params;
        private String operational;
        private String value;
        private String relational;
        private Integer flag;
    }

}
