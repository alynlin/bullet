package com.unique.bullet.config;

import com.unique.bullet.common.Constants;
import com.unique.bullet.exception.BulletException;
import com.unique.bullet.publish.PublishFactoryBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class PublishBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private final static Logger logger = LogManager.getLogger(PublishBeanDefinitionParser.class);


    private final static String INTERFACE_ATTRIBUTE = "interface";
    private final static String TTL_ATTRIBUTE = "ttl";
    private final static String TIMEOUT_ATTRIBUTE = "timeout";
    private final static String ROUTINGKEY_ATTRIBUTE = "routingKey";
    private final static String DESTINATION_ATTRIBUTE = "destination";
    private final static String CONNECTION_ATTRIBUTE = "connection";

    @Override
    protected Class<?> getBeanClass(Element element) {
        return PublishFactoryBean.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();
        beanDefinition.setBeanClass(PublishFactoryBean.class);

        String interfaze = element.getAttribute(INTERFACE_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(interfaze)) {
            throw new BulletException("<rabbit:service> '" + INTERFACE_ATTRIBUTE + "' can't be blank");
        }
        try {
            Class clazz = Class.forName(interfaze);
        } catch (ClassNotFoundException e) {
            throw new BulletException(e);
        }
        builder.addPropertyValue(INTERFACE_ATTRIBUTE, interfaze);
        //
        String ttl = element.getAttribute(TTL_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(ttl)) {
            ttl = String.valueOf(Constants.DEFAULT_TTL);
        }
        builder.addPropertyValue(TTL_ATTRIBUTE, Integer.valueOf(ttl));
        //
        String routingKey = element.getAttribute(ROUTINGKEY_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(routingKey)) {
            routingKey = String.valueOf(Constants.DEFAULT_ROUTINGKEY);
        }
        builder.addPropertyValue(ROUTINGKEY_ATTRIBUTE, routingKey);

        String destination = element.getAttribute(DESTINATION_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(destination)) {
            throw new BulletException("<bullet:publish> " + DESTINATION_ATTRIBUTE + " attribute can't be blank");
        }
        builder.addPropertyValue(DESTINATION_ATTRIBUTE, destination);

        //
        Object connection = new RuntimeBeanReference(element.getAttribute(CONNECTION_ATTRIBUTE));
        builder.addPropertyValue(CONNECTION_ATTRIBUTE, connection);


    }


}
