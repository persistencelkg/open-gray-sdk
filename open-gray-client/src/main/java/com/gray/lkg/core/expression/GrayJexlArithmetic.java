package com.gray.lkg.core.expression;

import org.apache.commons.jexl3.JexlArithmetic;

import java.lang.reflect.Array;
import java.math.MathContext;
import java.util.Objects;

/**
 * 为了支持数组比较
 */
public class GrayJexlArithmetic extends JexlArithmetic {

    public GrayJexlArithmetic() {
        this(false);
    }

    public GrayJexlArithmetic(boolean astrict) {
        this(astrict, null, Integer.MIN_VALUE);
    }

    public GrayJexlArithmetic(boolean astrict, MathContext bigdContext, int bigdScale) {
        super(astrict, bigdContext, bigdScale);
    }

    @Override
    public Boolean contains(Object container, Object value) {
        if (container == null || !container.getClass().isArray()) {
            return super.contains(container, value);
        }
        int length = Array.getLength(container);
        for (int i=0; i< length; i++) {
            Object left = Array.get(container, i);
            if (left instanceof Number && value instanceof Number &&
                ((Number)left).doubleValue() == ((Number)value).doubleValue()) {
                return true;
            }
            if (Objects.equals(left, value)) {
                return true;
            }
        }
        return false;
    }
}
