package com.gray.lkg.core.service_impl;

import com.gray.lkg.client.AbstractGrayPollClient;
import io.github.persistence.LongPoolConfig;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 11:22 AM
 */
public class DefaultGraySwitchClient extends AbstractGrayPollClient {

    public DefaultGraySwitchClient(LongPoolConfig longPoolConfig) {
        super(longPoolConfig);
    }

}
