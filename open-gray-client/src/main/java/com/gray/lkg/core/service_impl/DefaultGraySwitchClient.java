package com.gray.lkg.core.service_impl;

import com.gray.lkg.client.AbstractGrayPollClient;
import io.github.persistence.LongPoolConfig;

/**
 * TODO 通过注解 + JavaAgent 实现自定义静态方法、普通方法的流量拦截
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 11:22 AM
 */
public class DefaultGraySwitchClient extends AbstractGrayPollClient {

    public DefaultGraySwitchClient(LongPoolConfig longPoolConfig) {
        super(longPoolConfig);
    }

}
