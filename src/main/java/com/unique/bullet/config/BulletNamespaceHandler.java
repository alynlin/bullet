package com.unique.bullet.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Namespace handler for rocket
 */
public class BulletNamespaceHandler extends NamespaceHandlerSupport {


    public void init() {
        SystemInit.init();
        registerBeanDefinitionParser("publish", new PublishBeanDefinitionParser());
        registerBeanDefinitionParser("listener-container", new ListenerContainerParser());
        registerBeanDefinitionParser("protocol", new ProtocolBeanParser());
    }
}
