package com.gray.lkg.client;

import com.gray.lkg.core.GrayDispatchManager;
import com.gray.lkg.core.GraySwitchService;
import com.gray.lkg.model.*;
import io.github.persistence.BasicLongPollClient;
import io.github.persistence.LongPoolConfig;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.DynamicConfigManger;
import org.lkg.enums.TrueFalseEnum;
import org.lkg.request.InternalRequest;
import org.lkg.request.InternalResponse;
import org.lkg.request.SimpleRequestUtil;
import org.lkg.simple.ObjectUtil;
import org.lkg.simple.ServerInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: 李开广
 * @date: 2023/7/17 9:58 PM
 */
@Slf4j
public abstract class AbstractGrayPollClient extends BasicLongPollClient implements GraySwitchService {

    private final Map<String, Object> params;

    private final Map<String, GraySwitchVo> LOCAL_CACHE = new HashMap<>();

    protected AbstractGrayPollClient(int longPollInterval, boolean enableLongPool, LongPoolConfig longPoolConfig) {
        super(longPollInterval, enableLongPool, longPoolConfig);
        params = new HashMap<>();
        params.put("gray_version", "v1.0");
        params.put("server_name", ServerInfo.name());
    }

    @Override
    public List<GraySwitchVo> listAllGraySwitch(GrayTypeEnum grayTypeEnum) {
        if (Objects.isNull(grayTypeEnum)) {
            return new ArrayList<>(LOCAL_CACHE.values());
        }
        return LOCAL_CACHE.values().stream().filter(ref -> Objects.equals(ref.getGrayType(), grayTypeEnum.getCode())).collect(Collectors.toList());
    }

    @Override
    public GraySwitchVo getBySwitchName(String switchName) {
        return LOCAL_CACHE.get(switchName);
    }



    @Override
    protected void dealWithLongLink(LongPoolConfig longPoolConfig) {
        // 对于服务端推送结果 需要检查是否not modify、版本比较
        InternalResponse response = SimpleRequestUtil.request(InternalRequest.createPostRequest(longPoolConfig.getLongLinkUrl(), InternalRequest.BodyEnum.RAW, params));
        if (response.is4XXFail()) {
            handleNewStrategyList(null);
            return;
        }
        // Not modify
        if (response.getStatusCode() >= 300) {
            return;
        }
        if (response.is2XXSuccess()) {
            GraySwitchResponse entity = response.toEntity(GraySwitchResponse.class);
            if (Objects.nonNull(entity) && Objects.nonNull(entity.getData())) {
                List<GraySwitchVo> data = entity.getData();
                handleNewStrategyList(data);
            } else {
                handleNewStrategyList(null);
            }
        }
    }


    @Override
    protected void loadData(LongPoolConfig longPoolConfig) {
        InternalResponse response = SimpleRequestUtil.request(InternalRequest.createPostRequest(longPoolConfig.getPollUrl(), InternalRequest.BodyEnum.RAW, params));
        if (response.is2XXSuccess()) {
            // 处理数据
            GraySwitchResponse entity = response.toEntity(GraySwitchResponse.class);
            if (Objects.nonNull(entity) && Objects.nonNull(entity.getData())) {
                List<GraySwitchVo> data = entity.getData();
                // 主动拉取不需要版本控制， 因为从服务端拉取的肯定是最新的
                handleNewStrategyListWithOutVersion(data);
            }
            log.info("success load gray strategy:{}", entity);
        } else {
            log.error("fail long poll get data, reason:{}", response.getExceptionList());
        }
    }

    private void handleNewStrategyListWithOutVersion(List<GraySwitchVo> list) {
        handleNewStrategyList(list, false);
    }

    private void handleNewStrategyList(List<GraySwitchVo> list) {
        handleNewStrategyList(list, true);
    }

    private void handleNewStrategyList(List<GraySwitchVo> list, boolean needCompareVersion) {
        HashMap<String, GraySwitchVo> back = new HashMap<>(LOCAL_CACHE);
        if (ObjectUtil.isNotEmpty(list)) {
            // check switch version
            for (GraySwitchVo graySwitchVo : list) {
                String switchName = graySwitchVo.getSwitchName();
                GraySwitchVo oldVo = back.get(switchName);
                if (needCompareVersion &&
                        Objects.nonNull(oldVo) && graySwitchVo.getVersion() <= oldVo.getVersion()) {
                    continue;
                }
                // 条件匹配
                if (!matchCondition(graySwitchVo)) {
                    continue;
                }
                // 处理新的
                LOCAL_CACHE.put(switchName, graySwitchVo);
                GraySwitchVo remove = back.remove(switchName);
                // 分发新的
                if (!Objects.equals(remove, graySwitchVo)) {
                    GrayDispatchManager.dispatch(new GrayEvent(switchName, remove, graySwitchVo));
                    log.debug("gray switch change from:{} to:{}", remove, graySwitchVo);
                }
            }
        }

    }

    private boolean matchCondition(GraySwitchVo graySwitchVo) {
        // 状态是否开启
        if (Objects.equals(ControlEnum.ALL_OLD.getType(), graySwitchVo.getGrayType()) || TrueFalseEnum.isFalse(graySwitchVo.getStatus())) {
            return false;
        }
        // 全选默认匹配
        if (TrueFalseEnum.isTrue(graySwitchVo.getChooseAll())) {
            return true;
        }
        // 机器是否匹配 [需要配合sre 指定机器]
        List<String> instanceList = graySwitchVo.getInstanceList();
        if (ObjectUtil.isNotEmpty(instanceList)) {
            boolean bottomInLineMatch = instanceList.contains(ServerInfo.innerIp());
            return instanceList.contains(System.getProperty(DynamicConfigManger.getConfigValue("local-instance-name", "INSTANCE_NAME"))) || bottomInLineMatch;
        }
        return false;
    }
}
