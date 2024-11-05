package com.gray.lkg.core;

import com.gray.lkg.model.GrayEvent;
import com.gray.lkg.model.GraySwitchVo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lkg.simple.ObjectUtil;

import java.util.*;
import java.util.function.Consumer;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/1 9:55 PM
 */
@Slf4j
public class GrayDispatchManager {


    /**
     * 有时候需要对一个key执行多个不同事件操作
     */
    private static final Map<String, List<Consumer<GrayEvent>>> EVENT_CONSUME_MAP = new HashMap<>();

    private static final List<Consumer<GrayEvent>> list = new ArrayList<>();

    @Getter
    private static GraySwitchService graySwitchService;


    private static void addGrayEvent(String key, Consumer<GrayEvent> consumer) {
        EVENT_CONSUME_MAP.computeIfAbsent(key, ref -> new ArrayList<>()).add(consumer);
    }

    public static void registerAndTriggerGrayEvent(String key, Consumer<GraySwitchVo> consumer) {
        if (Objects.isNull(graySwitchService)) {
            return;
        }
        GraySwitchVo switchVo = graySwitchService.getBySwitchName(key);
        // register
        Consumer<GrayEvent> grayEventConsumer = grayEvent -> consumer.accept(grayEvent.getNewSwitch());
        addGrayEvent(key, grayEventConsumer);
        // trigger
        consumer.accept(switchVo);
    }


    public static void addGrayEvent(Consumer<GrayEvent> grayEventConsumer) {
        list.add(grayEventConsumer);
    }


    public static void dispatch(GrayEvent grayEvent) {
        List<Consumer<GrayEvent>> consumers = EVENT_CONSUME_MAP.get(grayEvent.getKey());
        if (ObjectUtil.isNotEmpty(consumers)) {
            consumers.forEach(ref -> ref.accept(grayEvent));
        }
        if (ObjectUtil.isNotEmpty(list)) {
            list.forEach(ref -> ref.accept(grayEvent));
        }
    }

}
