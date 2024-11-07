package com.gray.lkg.client;

import com.gray.lkg.core.GrayDispatchManager;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/17 9:08 PM
 */
@Slf4j
public final class GrayClient extends GraySwitchDispatcher{
    private static final Map<String, GrayClient> POOL = new ConcurrentHashMap<>();


    public GrayClient(String switchName) {
        super(switchName);
    }

    public static boolean containsSwitch(String switchName) {
        return POOL.containsKey(switchName);
    }

    public static GrayClient getInstance(String switchName) {
        if (POOL.containsKey(switchName)) {
            return POOL.get(switchName);
        }
        GrayClient grayClient = new GrayClient(switchName);
        if (GrayDispatchManager.containsSwitch(switchName)) {
            POOL.put(switchName, grayClient);
        } else {
            log.warn("not config gray switch key:{}", switchName);
        }
        return grayClient;
    }

    public void close(){
        POOL.remove(getSwitchName());
    }
}
