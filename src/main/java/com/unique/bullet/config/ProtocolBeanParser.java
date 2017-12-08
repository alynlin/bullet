package com.unique.bullet.config;

import com.unique.bullet.common.Constants;
import com.unique.bullet.connection.ConnectionFactoryBean;
import com.unique.bullet.exception.BulletException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ProtocolBeanParser extends AbstractSingleBeanDefinitionParser {

    private final static Logger logger = LoggerFactory.getLogger(ProtocolBeanParser.class);

    private final static String USERNAME_ATTRIBUTE = "username";
    private final static String PASSWORD_ATTRIBUTE = "password";
    private final static String ADDRESS_ATTRIBUTE = "addresses";
    private final static String SYSTEM_GROUP = "sysgroup";

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ConnectionFactoryBean.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();
        beanDefinition.setBeanClass(ConnectionFactoryBean.class);
        String username = element.getAttribute(USERNAME_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(username)) {
            username = Constants.DEFAULT_USERNAME;
        }
        builder.addPropertyValue(USERNAME_ATTRIBUTE, username);
        //
        String password = element.getAttribute(PASSWORD_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(password)) {
            password = Constants.DEFAULT_PASSWORD;
        }
        builder.addPropertyValue(PASSWORD_ATTRIBUTE, password);

        String group = element.getAttribute(SYSTEM_GROUP);
        if (StringUtils.isAnyEmpty(group)) {
            throw new BulletException("<bullet:connection> " + SYSTEM_GROUP + " attribute can't be blank");
        }
        builder.addPropertyValue(SYSTEM_GROUP, group);

        String address = element.getAttribute(ADDRESS_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(address)) {
            throw new BulletException("<bullet:connection> " + ADDRESS_ATTRIBUTE + " attribute can't be blank");
        }
        builder.addPropertyValue(ADDRESS_ATTRIBUTE, address);


    }
}
