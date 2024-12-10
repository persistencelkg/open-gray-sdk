package com.kg.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/12/10 4:15 PM
 */
@RestController
@RequestMapping("/feign")
@Slf4j
public class FeignController {


    @PostMapping("/old-uri")
    public String oldUri() {
        return "old uri";
    }

    @PostMapping("/new-uri")
    public String newUri() {
        return "new uri";
    }

}
