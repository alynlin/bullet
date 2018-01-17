package com.unique.bullet.config;

import com.unique.bullet.common.Constants;
import com.unique.bullet.connection.ConnectionFactoryBean;
import com.unique.bullet.exception.BulletException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ProtocolBeanParser extends AbstractSingleBeanDefinitionParser {

    private final static Logger logger = LogManager.getLogger(ProtocolBeanParser.class);

    private final static String USERNAME_ATTRIBUTE = "username";
    private final static String PASSWORD_ATTRIBUTE = "password";
    private final static String ADDRESS_ATTRIBUTE = "addresses";
    private final static String SYSTEM_GROUP = "sysgroup";
    private final static String PERMITS_SIZE_ATTRIBUTE = "permits-size";
    private final static String SENDMSG_TIMEOUT_ATTRIBUTE = "sendMsgTimeout";
    private final static String NAMESRV_DOMAIN_ELEMENT = "namesrv-domain";
    private final static String NAMESRV_DOMAIN_SUBGROUP_ELEMENT = "namesrv-domain-subgroup";

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
        if (StringUtils.isNoneEmpty(address)) {
            builder.addPropertyValue(ADDRESS_ATTRIBUTE, address);
        }

        String sendMsgTimeout = element.getAttribute(SENDMSG_TIMEOUT_ATTRIBUTE);
        if (StringUtils.isNoneEmpty(sendMsgTimeout)) {
            builder.addPropertyValue(SENDMSG_TIMEOUT_ATTRIBUTE, sendMsgTimeout);
        }

        String permits = element.getAttribute(PERMITS_SIZE_ATTRIBUTE);
        if (StringUtils.isNoneEmpty(permits)) {
            builder.addPropertyValue("permits", permits);
        }

        String namesrvDdomain = element.getAttribute(NAMESRV_DOMAIN_ELEMENT);
        if (StringUtils.isNoneEmpty(namesrvDdomain)) {
            System.setProperty("rocketmq.namesrv.domain", namesrvDdomain);
        }
        String namesrvDomainSubgroup = element.getAttribute(NAMESRV_DOMAIN_SUBGROUP_ELEMENT);
        if (StringUtils.isNoneEmpty(namesrvDomainSubgroup)) {
            System.setProperty("rocketmq.namesrv.domain.subgroup", namesrvDomainSubgroup);
        }
    }
}
