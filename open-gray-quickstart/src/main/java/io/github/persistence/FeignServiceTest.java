package io.github.persistence;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/12/10 4:08 PM
 */
@FeignClient(name = "open-gray-server", fallbackFactory = FeignFallback.class)
public interface FeignServiceTest {

    @PostMapping("/feign/old-uri")
    public String oldUri();

    @PostMapping("/feign/new-uri")
    String newUri();

}
