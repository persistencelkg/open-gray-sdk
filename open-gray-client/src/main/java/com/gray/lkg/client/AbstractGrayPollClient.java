package com.gray.lkg.client;

import com.gray.lkg.model.GraySwitchVo;
import io.github.persistence.BasicLongPollClient;
import io.github.persistence.LongPoolConfig;
import lombok.extern.slf4j.Slf4j;
import org.lkg.request.InternalRequest;
import org.lkg.request.InternalResponse;
import org.lkg.request.SimpleRequestUtil;
import org.lkg.simple.ServerInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/17 9:58 PM
 */
@Slf4j
public abstract class AbstractGrayPollClient extends BasicLongPollClient {

    private final Map<String, Object> params;
    private String version;
    protected AbstractGrayPollClient(int longPollInterval, boolean enableLongPool, LongPoolConfig longPoolConfig) {
        super(longPollInterval, enableLongPool, longPoolConfig);
        params = new HashMap<>();
        params.put("gray_version", "v1.0");
        params.put("server_name", ServerInfo.name());
    }


    @Override
    protected void dealWithLongLink(LongPoolConfig longPoolConfig) {

    }


    @Override
    protected void loadData(LongPoolConfig longPoolConfig) {
        InternalResponse response = SimpleRequestUtil.request(InternalRequest.createPostRequest(longPoolConfig.getPollUrl(), InternalRequest.BodyEnum.RAW, params));
        if (response.is2XXSuccess()) {
            // 处理数据

            log.info("load gray strategy:{}", 0);
        } else {
            log.error("long poll get data fail:{}", response.getExceptionList());
        }
    }


    private void handleNewStrategyList(List<GraySwitchVo> list) {
        //
    }
}
