package com.gray.lkg.core;

import org.lkg.metric.api.CommonFilter;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Description: 支持从header获取灰度参数pairs
 * Author: 李开广
 * Date: 2024/11/6 11:17 AM
 */
public class ParamParseFilter implements CommonFilter {


    @Override
    public void filter(SelfChain selfChain) throws ServletException, IOException {
        try {
            selfChain.proceed();
        } finally {

        }

    }

    @Override
    public int getOrder() {
        return 0;
    }


}
