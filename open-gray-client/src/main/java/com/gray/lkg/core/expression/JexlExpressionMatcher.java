package com.gray.lkg.core.expression;

import com.gray.lkg.core.GrayExpressionMatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import java.util.Map;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 3:14 PM
 */
@Slf4j
public class JexlExpressionMatcher implements GrayExpressionMatcher {

    private JexlExpression expression;

    private final JexlEngine engine;


    public JexlExpressionMatcher() {
        engine = new JexlBuilder().arithmetic(new GrayJexlArithmetic(false)).create();

    }

    @Override
    public void setExpression(String expression) {
        this.expression = engine.createExpression(expression);
    }

    @Override
    public String getExpression() {
        return expression.getSourceText();
    }

    @Override
    public boolean match(Map<String, Object> args) {
        MapContext mapContext = new MapContext(args);
        try {
            Object evaluate = this.expression.evaluate(mapContext);
            return Objects.equals(evaluate, Boolean.TRUE);
        } catch (Exception e) {
            log.error("parse expression:{} fail, args:{}", getExpression(), args);
        }
        return false;

    }
}
