package com.gray.lkg.intercepter.aop;

import feign.Contract;
import feign.MethodMetadata;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.env.Environment;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/21 6:31 PM
 */
public class AopFeignMethodInterceptor implements MethodInterceptor {

    private Environment environment;

    public AopFeignMethodInterceptor(Environment environment ) {
        this.environment = environment;
    }
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object res = null;
        try {
            res = methodInvocation.proceed();
        } finally {
            Object[] arguments = methodInvocation.getArguments();
            if (Objects.nonNull(arguments) &&  arguments[0] != null && arguments[0] instanceof Class) {
                addFeignUrl((Class<?>) arguments[0], res);
            }
        }
        return res;
    }

    private void addFeignUrl(Class<?> feignInterfaceMethod, Object res) {
        FeignClient annotation = feignInterfaceMethod.getAnnotation(FeignClient.class);
        String url = annotation.url();
        // 或缺不到从环境中降级
        if (Objects.isNull(url)) {
            url =  environment.resolvePlaceholders(annotation.name());
        }
        if (Objects.isNull(url)) {
            url = environment.resolvePlaceholders(annotation.value());
        }
        // 只考虑 返回值 List<MethodMetadata>， 应该是feign启东时扫描代理的类
        if (res instanceof Collection) {
            Collection<?> collection = (Collection<?>) res;
            collection.stream()
                    .filter(ref -> ref instanceof MethodMetadata)
                    .map(ref -> (MethodMetadata) ref)
                    .forEach(ref -> {

                    });
                ;
        }
    }


   public static Object getProxy(Environment environment, Contract delegate){
        ProxyFactoryBean factory = new ProxyFactoryBean();
        factory.setProxyTargetClass(true);
        factory.addAdvice(new AopFeignMethodInterceptor(environment));
        factory.setTarget(delegate);
        return factory.getObject();
    }

}
