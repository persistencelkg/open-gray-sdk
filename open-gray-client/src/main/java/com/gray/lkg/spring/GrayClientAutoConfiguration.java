package com.gray.lkg.spring;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.gray.lkg.config.GrayConst;
import com.gray.lkg.config.OnGrayEnable;
import com.gray.lkg.core.GrayDispatchManager;
import com.gray.lkg.core.GraySwitchService;
import com.gray.lkg.core.ParamParseFilter;
import com.gray.lkg.core.flow.*;
import com.gray.lkg.core.service_impl.DefaultGraySwitchClient;
import feign.Feign;
import feign.Request;
import io.github.persistence.LongPoolConfig;
import lombok.extern.slf4j.Slf4j;
import org.lkg.ding.DingDingMsg;
import org.lkg.ding.DingDingUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.gray.lkg.config.GrayConst.GRAY_LONG_POLL_CONFIG_PREFIX;

/**
 * @description: 灰度客户端的配置类
 * @author: 李开广
 * @date: 2023/7/14 4:18 PM
 */
@Configuration
@OnGrayEnable
@Slf4j
public class GrayClientAutoConfiguration implements SmartInitializingSingleton {

    public void init() {
        GraySwitchService graySwitchService = GrayDispatchManager.getGraySwitchService();
        if (Objects.isNull(graySwitchService)) {
            log.error("gray client init fail");
            DingDingUtil.sendMessage(DingDingMsg.createText("gray client init fail"), GrayConst.GRAY_URL, GrayConst.GRAY_SECRET, true);
        }
    }


    @ConfigurationProperties(prefix = GRAY_LONG_POLL_CONFIG_PREFIX)
    @Bean(name = "grayLongPoolConfig")
    public LongPoolConfig grayLongPoolConfig() {
        return new LongPoolConfig();
    }


    @Bean
    public ParamParseFilter paramParseFilter() {
        return new ParamParseFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public GraySwitchService graySwitchService(LongPoolConfig grayLongPoolConfig) {
        DefaultGraySwitchClient defaultGraySwitchClient = new DefaultGraySwitchClient(grayLongPoolConfig);
        GrayDispatchManager.setGraySwitchService(defaultGraySwitchClient);
        return defaultGraySwitchClient;
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.init();
    }


    @Configuration
    @ConditionalOnClass(value = {Feign.class, Request.class})
    static class FeignFlowGrayAutoConfiguration {

        @Bean
        public FeignGrayInterceptor feignGrayInterceptor(GraySwitchService graySwitchService) {
            return new FeignGrayInterceptor(graySwitchService);
        }
    }


    @Configuration
    @ConditionalOnClass(RestTemplate.class)
    static class RestTemplateFlowGrayAutoConfiguration {

        @Bean
        public RestTemplateGrayInterceptorSmartInitializingSingleton restTemplateGrayInterceptorSmartInitializingSingleton(ObjectProvider<RestTemplate> restTemplateObjectProvider, GraySwitchService graySwitchService) {
            return new RestTemplateGrayInterceptorSmartInitializingSingleton(restTemplateObjectProvider, graySwitchService);
        }

        static class RestTemplateGrayInterceptorSmartInitializingSingleton implements SmartInitializingSingleton {
            private final Iterator<RestTemplate> iterator;
            private final GraySwitchService graySwitchService;

            public RestTemplateGrayInterceptorSmartInitializingSingleton(ObjectProvider<RestTemplate> restTemplateObjectProvider, GraySwitchService graySwitchService) {
                this.iterator = restTemplateObjectProvider.stream().iterator();
                this.graySwitchService = graySwitchService;
            }

            @Override
            public void afterSingletonsInstantiated() {
                iterator.forEachRemaining(ref -> {
                    List<ClientHttpRequestInterceptor> interceptors = ref.getInterceptors();
                    interceptors.add(0, new RestTemplateGrayInterceptor(graySwitchService));
                });
            }
        }
    }


    @Configuration
    @ConditionalOnProperty({"gray.server.name", "gray.server.registerInfoUrl", "gray.nacos.address"})
    static class FLowGrayConfiguration {

        @Bean
        @ConditionalOnProperty(prefix = "gray.nacos", value = {"address"})
        public FlowGrayInfoLoader nacosRegisterInfoLoader(NacosServiceManager nacosServiceManager, NacosDiscoveryProperties nacosDiscoveryProperties) {
            return new FlowGrayInfoLoader(nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties()), nacosDiscoveryProperties);
        }


        @Bean
        @ConditionalOnMissingBean
        public FlowGrayRegister defaultFlowGrayRegister(FlowGrayInfoLoader flowGrayInfoLoader) {
            return new DefaultFlowGrayRegister(flowGrayInfoLoader);
        }


        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean(NacosAutoServiceRegistration.class)
        public FlowGrayRegisterAwareListener selfAdaptGrayRegister(NacosRegistration registration, FlowGrayRegister defaultFlowGrayRegister) {
            return new FlowGrayRegisterAwareListener(defaultFlowGrayRegister, registration);
        }

    }

}
