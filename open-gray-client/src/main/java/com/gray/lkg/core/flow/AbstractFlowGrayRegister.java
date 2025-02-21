package com.gray.lkg.core.flow;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.gray.lkg.config.GrayConst;
import com.gray.lkg.model.GrayServerRegisterInfoResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lkg.enums.TrueFalseEnum;
import org.lkg.utils.ObjectUtil;
import org.lkg.utils.ServerInfo;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description:
 * Author: 李开广
 * Date: 2025/2/11 2:38 PM
 */
@Slf4j
@AllArgsConstructor
public abstract class AbstractFlowGrayRegister implements FlowGrayRegister {


    private final FlowGrayInfoLoader flowGrayInfoLoader;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final AtomicBoolean registered = new AtomicBoolean();

    @Override
    public void register() {
        String instanceName = GrayConst.getInstanceName();
        if (!reentrantLock.tryLock() || registered.get()) {
            log.info("current instance name:{} has registered", instanceName);
            return;
        }
        try {
            reentrantLock.lock();

            String serverName = ServerInfo.name();
            String ip = ServerInfo.innerIp();
            Integer port = ServerInfo.port();
            log.info("current instance name:{}", instanceName);
            List<GrayServerRegisterInfoResponse> registerInfoResponses = flowGrayInfoLoader.loadGrayList(serverName, instanceName, port);
            boolean isGray = false;
            for (GrayServerRegisterInfoResponse registerInfo : registerInfoResponses) {
                if (!Objects.equals(registerInfo.getServerName(), serverName)
                        || !Objects.equals(ip, registerInfo.getIp())
                        || !Objects.equals(port, registerInfo.getPort())) {
                    continue;
                }
                log.info("found graying instance:{}, will enable namespace:{}", instanceName, registerInfo.getGrayServerName());
                boolean allOpen = TrueFalseEnum.isTrue(registerInfo.getStatus()) && TrueFalseEnum.isTrue(registerInfo.getControlType());
                registerWithBeat(allOpen, registerInfo.getGrayServerName(), ip, port, true);
                if (TrueFalseEnum.isTrue(registerInfo.getStatus()) || TrueFalseEnum.isTrue(registerInfo.getControlType())) {
                    isGray = true;
                }
            }
            if (ObjectUtil.isEmpty(registerInfoResponses)) {
                registerWithBeat(true, serverName, ip, port, false);
            } else {
                // 从某种意义上来说，有灰度节点可以不挂，取决运维平台是否会手动干预，如果干预就需要避免影响CI/CD流程
                flowGrayInfoLoader.registerWithOutBeatAndNoWeight(serverName, ip, port, isGray);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    private void registerWithBeat(boolean enable, String serverName, String ip, Integer port, boolean grayNode) {
        try {
            NamingService namingService = flowGrayInfoLoader.getNamingService();
            namingService.deregisterInstance(serverName, ip, port);
            Instance instance = new Instance();
            instance.setServiceName(serverName);
            instance.setIp(ip);
            instance.setPort(port);
            instance.setEnabled(enable);
            if (grayNode) {
                // 剔除曾经挂靠的实例
                List<Instance> allInstances = namingService.getAllInstances(serverName);
                if (ObjectUtil.isNotEmpty(allInstances)) {
                    for (Instance val : allInstances) {
                        flowGrayInfoLoader.updateInstance(serverName, val.getIp(), val.getPort(), false);
                        namingService.deregisterInstance(serverName, val.getIp(), val.getPort());
                    }
                }
            }
            namingService.registerInstance(serverName, instance);
            log.info("server:{} registered, enable:{}", serverName, enable);
        } catch (NacosException e) {
            log.error("gray auto register fail:", e);
            throw new RuntimeException(e);
        }
    }


}
