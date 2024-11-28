package io.github.persistence;

import com.gray.lkg.client.GrayClient;
import org.lkg.config.ApolloConfigBeanFactoryPostProcessorInitializer;
import org.lkg.config.DynamicConfigAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 5:20 PM
 */
@SpringBootApplication
//@Import(value = {DynamicConfigAutoConfiguration.class, ApolloConfigBeanFactoryPostProcessorInitializer.class})
public class GrayQuickStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrayQuickStartApplication.class, args);
    }

}

