package com.gray.lkg.config;

import com.gray.lkg.intercepter.filter.CommonFilterInterceptor;
import com.gray.lkg.intercepter.filter.CommonWebFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import java.util.List;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/14 5:06 PM
 */
@Configuration
@ConditionalOnClass(CommonWebFilter.class)
public class CommonWebFilterAutoConfiguration {

    private final List<CommonFilterInterceptor> commonFilterInterceptorList;

    public CommonWebFilterAutoConfiguration(List<CommonFilterInterceptor> commonFilterInterceptorList) {
        this.commonFilterInterceptorList = commonFilterInterceptorList;
    }

    @Bean
    public FilterRegistrationBean<CommonWebFilter> commWebFilter() {
        FilterRegistrationBean<CommonWebFilter> filterRegistrationBean = new FilterRegistrationBean<>(new CommonWebFilter(commonFilterInterceptorList));
        // 最优先访问
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.REQUEST);
        return filterRegistrationBean;
    }


}
