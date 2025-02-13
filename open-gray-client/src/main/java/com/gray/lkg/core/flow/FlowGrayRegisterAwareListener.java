package com.gray.lkg.core.flow;

import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.gray.lkg.config.GrayConst;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.DynamicConfigManger;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.cloud.client.discovery.event.InstancePreRegisteredEvent;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description:
 * Author: 李开广
 * Date: 2025/2/11 1:57 PM
 */
@Slf4j
public abstract class FlowGrayRegisterAwareListener implements ApplicationContextAware, ApplicationListener<WebServerInitializedEvent> {

    private ApplicationContext applicationContext;
    private Environment environment;
    private final AtomicBoolean running = new AtomicBoolean();

    private final FlowGrayRegister flowGrayRegister;
    private final NacosRegistration nacosRegistration;

    public FlowGrayRegisterAwareListener(FlowGrayRegister flowGrayRegister, NacosRegistration nacosRegistration) {
        this.flowGrayRegister = flowGrayRegister;
        this.nacosRegistration = nacosRegistration;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.environment = applicationContext.getEnvironment();
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        if (nacosRegistration.isRegisterEnabled()) {
            return;
        }
        if (Objects.isNull(flowGrayRegister)) {
            log.warn("gray auto register fail, not found gray info loader");
            return;
        }
        if (!running.get() && DynamicConfigManger.getBoolean(GrayConst.FLOW_GRAY_AUTO_REGISTER_ENABLE_KEY)) {
            this.applicationContext.publishEvent(
                    new InstancePreRegisteredEvent(this, nacosRegistration));
            flowGrayRegister.register();
            this.applicationContext.publishEvent(
                    new InstanceRegisteredEvent<>(this, nacosRegistration));
            running.compareAndSet(false, true);
        }
    }

}
