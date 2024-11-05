package com.gray.lkg.core;

/**
 * 灰度干预执行器
 * Description:
 * Author: 李开广
 * Date: 2024/11/5 8:14 PM
 */
public interface GrayExecutor<Req, Resp> {

    Resp execute() throws Throwable;

    Resp execute(Req req) throws Throwable;

    /**
     * 新接口迁移场景
     */
    Resp execute(String url) throws Throwable;
}
