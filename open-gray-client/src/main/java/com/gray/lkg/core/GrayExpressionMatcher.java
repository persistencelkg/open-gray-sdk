package com.gray.lkg.core;

import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 3:12 PM
 */
public interface GrayExpressionMatcher {

    void setExpression(String expression);

    String getExpression();

    boolean match(Map<String, Object> args);
}
