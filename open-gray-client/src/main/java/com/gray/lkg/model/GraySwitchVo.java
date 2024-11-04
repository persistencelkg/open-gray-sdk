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

    private Long version;

    /**
     * 灰度类型 0 业务灰度  1 流量灰度
     */
    private Integer grayType;


    // ---------------- 灰度核心参数 ----------------

    private List<String> instanceList;
    private Integer chooseAll;

    private String oldDownStream;

    private String oldUrl;

    private String newDownStream;

    private String newUrl;

    /**
     * 灰度条件表达式
     */
    private String grayCondition;

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
    private GrayTime grayTime;

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
    private static class GrayTime {

        /**
         * 多少次
         */
        private Integer grayCount;

        /**
         * 固定基于秒
         */
        private Integer grayPeriod;
    }
}
