package com.unique.bullet.service.impl;

import com.unique.bullet.service.PublishService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PublishServiceImpl implements PublishService {

    private final static Logger log = LogManager.getLogger(PublishServiceImpl.class);

    public String sayHello(String name) {
        log.info("subscribe invoke~~~");
        return null;
    }
}
