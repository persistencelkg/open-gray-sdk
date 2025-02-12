package com.gray.lkg.config;

import org.lkg.core.DynamicConfigManger;
import org.lkg.utils.ServerInfo;

import java.util.Optional;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/6 1:40 PM
 */
public interface GrayConst {

    String GRAY_LONG_POLL_CONFIG_PREFIX = "open.gray.long-pool";

    String GRAY_LONG_POLL_INTERVAL_KEY = "open.gray.poll-interval";
    String GRAY_LONG_POLL_ENABLE_KEY = "open.gray.poll-enable";

    String FLOW_GRAY_AUTO_REGISTER_ENABLE_KEY="open.gray.register.enable";

    String GRAY_URL = "https://oapi.dingtalk.com/robot/send?access_token=795fdde5468a9d7b58c69bfdd50454e1fa47b8853cfb3db2542d679a7b3325a4";
    String GRAY_SECRET = "SEC3262aebfcabbe08b1657b6294e7389780de5da4b42effb240142f40762ba5a35";

    public static String getInstanceName() {
        String configKey = DynamicConfigManger.getConfigValue("local-instance-name", "INSTANCE_NAME");
        return Optional.ofNullable(System.getenv(configKey)).orElse(ServerInfo.getHostname());
    }
}
