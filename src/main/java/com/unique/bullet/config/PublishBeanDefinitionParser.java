package com.unique.bullet.config;

import com.unique.bullet.common.Constants;
import com.unique.bullet.common.SendMode;
import com.unique.bullet.common.Validators;
import com.unique.bullet.exception.BulletException;
import com.unique.bullet.listener.MessageListenerAdapter;
import com.unique.bullet.publish.PublishFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

public class PublishBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private final static Logger logger = LogManager.getLogger(PublishBeanDefinitionParser.class);


    private final static String INTERFACE_ATTRIBUTE = "interface";
    private final static String TTL_ATTRIBUTE = "ttl";
    private final static String TIMEOUT_ATTRIBUTE = "timeout";
    private final static String ROUTINGKEY_ATTRIBUTE = "routingKey";
    private final static String DESTINATION_ATTRIBUTE = "destination";
    private final static String CONNECTION_ATTRIBUTE = "connection";
    private final static String CODEC_ATTRIBUTE = "codec";
    private final static String SEND_MODE_ATTRIBUTE = "send-mode";
    private final static String DELAY_TIMELEVEL_ATTRIBUTE = "delayTimeLevel";
    private static final String FILTER_PROP_ELEMENT = "filter-prop";
    private static final String FILTER_PROP_ATTRIBUTE = "filterProp";
    private static final String SEND_CALLBACK_ATTRIBUTE = "sendCallback";
    private static final String FILTER_ANNOTATION_ELEMENT = "filter-annotation";
    private static final String FILTER_ANNOTATION_ATTRIBUTE = "filterAnnotation";

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
            throw new BulletException("<bullet:publish> '" + INTERFACE_ATTRIBUTE + "' can't be blank");
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
        builder.addPropertyValue(TTL_ATTRIBUTE, Long.valueOf(ttl));
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

        //编码方式，不指定的情况下，默认使用jdk
        String codec = element.getAttribute(CODEC_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(codec)) {
            codec = Constants.DEFAULT_CODEC;
        }
        builder.addPropertyValue(CODEC_ATTRIBUTE, codec);

        //通信方式
        String communicationMode = element.getAttribute(SEND_MODE_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(communicationMode)) {
            communicationMode = SendMode.ASYNC;
        }
        builder.addPropertyValue("communicationMode", communicationMode);

        String delayTimeLevel = element.getAttribute(DELAY_TIMELEVEL_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(delayTimeLevel)) {
            delayTimeLevel = "0";
        }
        builder.addPropertyValue(DELAY_TIMELEVEL_ATTRIBUTE, delayTimeLevel);

        Object connection = new RuntimeBeanReference(element.getAttribute(CONNECTION_ATTRIBUTE));
        builder.addPropertyValue(CONNECTION_ATTRIBUTE, connection);

        String sendCallBack = element.getAttribute(SEND_CALLBACK_ATTRIBUTE);
        if (StringUtils.isNoneEmpty(sendCallBack)) {
            builder.addPropertyValue(SEND_CALLBACK_ATTRIBUTE, new RuntimeBeanReference(sendCallBack));
        }

        String filterAnnotation = element.getAttribute(FILTER_ANNOTATION_ELEMENT);
        if (StringUtils.isNoneEmpty(filterAnnotation)) {
            builder.addPropertyValue(FILTER_ANNOTATION_ATTRIBUTE, Boolean.valueOf(filterAnnotation));
        }

        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String localName = parserContext.getDelegate().getLocalName(child);
                if (FILTER_PROP_ELEMENT.equals(localName)) {
                    builder.addPropertyValue(FILTER_PROP_ATTRIBUTE, parseMessageProperty((Element) child, element, parserContext));
                    break;
                }
            }
        }
    }

    private Map parseMessageProperty(Element messagePropertyEle, Element containerEle, ParserContext parserContext) {

        RootBeanDefinition messagePropertyDef = new RootBeanDefinition(MessageListenerAdapter.class);
        messagePropertyDef.setSource(parserContext.extractSource(messagePropertyEle));

        Map<?, ?> map = parserContext.getDelegate().parseMapElement(messagePropertyEle, messagePropertyDef);
        Validators.checkFilterProp(map);
        return map;
    }
}
