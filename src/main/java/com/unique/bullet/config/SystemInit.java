package com.unique.bullet.config;

import com.unique.bullet.common.SystemConfig;

public class SystemInit {

    static void init() {
        //日志级别，默认为error
        System.setProperty(SystemConfig.ROCKETMQ_CLIENT_LOGLEVEL, "error");
        System.setProperty("rocketmq.client.log.loadconfig", "false");
    }
}
