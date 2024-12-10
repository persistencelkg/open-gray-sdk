package io.github.persistence;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/12/10 4:10 PM
 */
//@Component
@Slf4j
public class FeignFallback implements FallbackFactory<FeignServiceTest> {
    @Override
    public FeignServiceTest create(Throwable throwable) {
        log.info(throwable.getMessage(), throwable);
        return null;
    }
}
