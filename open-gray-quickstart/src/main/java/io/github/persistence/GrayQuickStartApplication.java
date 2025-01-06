package io.github.persistence;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.lkg.apollo.EnableDynamicApollo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 5:20 PM
 */
@SpringBootApplication
@EnableDynamicApollo
@EnableFeignClients
public class GrayQuickStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrayQuickStartApplication.class, args);
    }

}

