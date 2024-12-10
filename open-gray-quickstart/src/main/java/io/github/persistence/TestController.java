package io.github.persistence;

import com.gray.lkg.client.GrayClient;
import org.lkg.request.InternalRequest;
import org.lkg.request.SimpleRequestUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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

    public static void main(String[] args) {
        //
        HashMap<String, Object> param = new HashMap<>();
        param.put("server_name", "open-gray-server");
        param.put("gray_version", null);
        System.out.println(SimpleRequestUtil.request(InternalRequest.createPostRequest("http://localhost:9999/gray/long-poll", InternalRequest.BodyEnum.RAW, param)));
    }


}
