package com.gray.lkg.core;

import com.gray.lkg.client.GrayClient;
import com.gray.lkg.model.GraySwitchVo;
import com.gray.lkg.model.GrayTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.lkg.enums.StringEnum;
import org.lkg.simple.ObjectUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 对基于新老url切换，基于流量灰度切换服务核心处理
 * Description:
 * Author: 李开广
 * Date: 2024/11/5 5:06 PM
 */
@Slf4j
public abstract class AbstractGrayInterceptor<Req, Resp> {
//
//    private final Map<String, GraySwitchVo> graySwitchMetaMap = new HashMap<>();
//    private final Map<String, GrayClient> clientMap = new HashMap<>();
    private final GraySwitchService graySwitchService;

    public AbstractGrayInterceptor(GraySwitchService graySwitchService) {
//        GrayDispatchManager.addGrayEvent(this::onGrayEvent);
        this.graySwitchService = graySwitchService;
//        if (Objects.nonNull(graySwitchService)) {
//            List<GraySwitchVo> graySwitchVos = graySwitchService.listAllGraySwitch();
//            Optional.ofNullable(graySwitchVos).ifPresent(ref -> ref.stream().map(GrayEvent::new).forEach(this::onGrayEvent));
//        }
    }

//    private void onGrayEvent(GrayEvent grayEvent) {
//        GraySwitchVo newSwitch = grayEvent.getNewSwitch();
//        if (Objects.isNull(newSwitch) && clientMap.containsKey(grayEvent.getKey())) {
//            clientMap.get(grayEvent.getKey()).close();
//        }
//        graySwitchMetaMap.put(grayEvent.getKey(), newSwitch);
//    }


    protected Resp flowIntercept(GrayExecutor<Req, Resp> executor, String url, String uri) throws IOException {
        List<GraySwitchVo> graySwitchVos = graySwitchService.listAllGraySwitch(GrayTypeEnum.FLOW_GRAY);
        if (ObjectUtil.isEmpty(graySwitchVos)) {
            return executor.execute();
        }
        GraySwitchVo graySwitchVo = directFindFlowGraySwitch(graySwitchVos, url, uri);
        if (Objects.isNull(graySwitchVo)) {
            return executor.execute();
        }
//        if (!GrayClient.containsSwitch(graySwitchVo.getSwitchName())) {
//            return executor.execute();
//        }
        // 基于uri 优先级最高
        if (ObjectUtil.isNotEmpty(graySwitchVo.getNewDownStream()) && ObjectUtil.isNotEmpty(graySwitchVo.getNewUri())) {
            String newURl = StringEnum.HTTP_PREFIX + graySwitchVo.getNewDownStream() + graySwitchVo.getNewUri();
            int i = url.indexOf("?");
            if (i > 0) {
                newURl += url.substring(i);
            }
            return grayFlow(graySwitchVo, executor, newURl);
        }
        // 说明是基于通用流量去访问临时服务
        return grayFlow(graySwitchVo, executor, url.replaceFirst(graySwitchVo.getOldDownStream(), graySwitchVo.getNewDownStream()));
    }

    private Resp grayFlow(GraySwitchVo graySwitchVo, GrayExecutor<Req, Resp> executor, String url) throws IOException {
        GraySwitchVo.GrayWeight grayWeight = graySwitchVo.getGrayWeight();
        try {
            if (grayWeight.hit()) {
                return executor.execute(url);
            }
        } catch (Exception e) {
            log.warn("url:{} gray flow fail:{}, bottom in line with old logic", url, e.getMessage());
        }
        return executor.execute();
    }

    private GraySwitchVo directFindFlowGraySwitch(List<GraySwitchVo> graySwitchVos, String url, String uri) {
        for (GraySwitchVo graySwitchVo : graySwitchVos) {
            if (!Objects.equals(graySwitchVo.getGrayType(), GrayTypeEnum.FLOW_GRAY.getCode())) {
                continue;
            }
            // 基于uri的流量灰度
            if (url.contains(graySwitchVo.getOldDownStream()) && uri.equals(graySwitchVo.getOldUri())) {
                return graySwitchVo;
            }
            // 基于全局流量灰度
            if (uri.equals(graySwitchVo.getOldUri())) {
                return graySwitchVo;
            }
        }
        return null;
    }

}
