package com.gray.lkg.core.flow;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.gray.lkg.config.GrayConst;
import com.gray.lkg.model.GrayServerRegisterInfoResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lkg.enums.TrueFalseEnum;
import org.lkg.utils.ServerInfo;

import java.util.List;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2025/2/11 2:38 PM
 */
@Slf4j
@AllArgsConstructor
public abstract class AbstractFlowGrayRegister implements FlowGrayRegister {


    private final FlowGrayInfoLoader flowGrayInfoLoader;

    @Override
    public void register() {
        String serverName = ServerInfo.name();
        String ip = ServerInfo.innerIp();
        Integer port = ServerInfo.port();
        String instanceName = GrayConst.getInstanceName();
        log.info("current instance name:{}", instanceName);
        List<GrayServerRegisterInfoResponse> registerInfoResponses = flowGrayInfoLoader.loadGrayList(serverName, instanceName, port);
        boolean originRegisterStatus = true;
        for (GrayServerRegisterInfoResponse registerInfo : registerInfoResponses) {
            if (!Objects.equals(registerInfo.getServerName(), serverName)
                    || !Objects.equals(ip, registerInfo.getIp())
                    || !Objects.equals(port, registerInfo.getPort())) {
                continue;
            }
            log.info("found graying instance:{}, will enable namespace:{}", instanceName, registerInfo.getGrayServerName());
            boolean allOpen = TrueFalseEnum.isTrue(registerInfo.getStatus()) && TrueFalseEnum.isTrue(registerInfo.getControlType());
            updateNacosStatus(allOpen, registerInfo.getGrayServerName(), ip, port);
            if (TrueFalseEnum.isTrue(registerInfo.getStatus()) || TrueFalseEnum.isTrue(registerInfo.getControlType())) {
                originRegisterStatus = false;
            }
        }
        updateNacosStatus(originRegisterStatus, serverName, ip, port);
    }

    private void updateNacosStatus(boolean enable, String serverName, String ip, Integer port) {
        try {
            NamingService namingService = flowGrayInfoLoader.getNamingService();
            namingService.deregisterInstance(serverName, ip, port);
            Instance instance = new Instance();
            instance.setServiceName(serverName);
            instance.setIp(ip);
            instance.setPort(port);
            instance.setEnabled(enable);
            namingService.registerInstance(serverName, instance);
            log.info("gray auto register:{} success, enable:{}", serverName, enable);
        } catch (NacosException e) {
            log.error("gray auto register fail:", e);
            throw new RuntimeException(e);
        }
    }
}
