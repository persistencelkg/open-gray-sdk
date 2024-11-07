package com.gray.lkg.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

import static com.gray.lkg.config.GrayConst.GRAY_LONG_POLL_CONFIG_PREFIX;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 11:12 AM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(prefix = GRAY_LONG_POLL_CONFIG_PREFIX, value = {"poll-url", "long-link-url", "domain"})
public @interface OnGrayEnable {
}
