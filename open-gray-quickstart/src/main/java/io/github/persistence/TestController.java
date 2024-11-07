package io.github.persistence;

import com.gray.lkg.client.GrayClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/7 10:46 AM
 */
@RestController
@RequestMapping("/test")
public class TestController {


    GrayClient client = GrayClient.getInstance("hit-gray");



    @GetMapping("/gray")
    public boolean testGray() {
        return client.get();
    }
}
