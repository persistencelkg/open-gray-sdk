package com.gray.lkg.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GrayRuleExpression {

    @JsonProperty("params")
    private String params;

    @JsonProperty("operational")
    private String operatorValue;

    private String value;

    @JsonProperty("relational")
    private String relationValue;

    /**
     * 只针对限制次数模式生效
     * 0 普通模式 1 特殊in模式
     */
    private int flag;

    public Relation getRelation() {
        return Relation.parse(relationValue);
    }

    public Operator getOperator() {
        return Operator.parse(operatorValue);
    }

    /**
     * 是否特殊in模式。如果指定了特殊in模式，那么在指定周期限制次数的模式中，次数限制指的是in条件里的每个参数的次数。比如 cityId in [100, 102]
     * 这个条件，在special 为true的情况下，灰度限流规则为1小时100次，指的是cityId为100和102的情况下各允许100次
     */
    public boolean isSpecial() {
        return flag == 1;
    }

    public enum Relation {
        Or("||"),
        And("&&");

        private final String value;

        Relation(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Relation parse(String str) {
            for (Relation relation : Relation.values()) {
                if (relation.name().equalsIgnoreCase(str) || relation.value.equals(str)) {
                    return relation;
                }
            }
            return null;
        }

    }

    public enum Operator {
        gt(">"),
        ge(">="),
        lt("<"),
        le("<="),
        eq("=="),
        neq("!="),
        in("=~"),
        not_in("!~")
        ;

        private final String value;

        Operator(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Operator parse(String str) {
            for (Operator operator : Operator.values()) {
                if (operator.name().equalsIgnoreCase(str) || operator.value.equalsIgnoreCase(str)) {
                    return operator;
                }
            }
            return null;
        }
    }
}
