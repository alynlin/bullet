package com.unique.bullet.config;

import com.unique.bullet.common.Constants;
import com.unique.bullet.exception.BulletException;
import com.unique.bullet.listener.ListenerFactoryBean;
import com.unique.bullet.listener.MessageListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ListenerContainerParser extends AbstractSingleBeanDefinitionParser {


    private static final String LISTENER_ELEMENT = "listener";
    private static final String ID_ATTRIBUTE = "id";
    private static final String DESTINATION_ATTRIBUTE = "destination";
    private static final String REF_ATTRIBUTE = "ref";
    private static final String MESSAGE_MODEL_ATTRIBUTE = "message-model";
    private static final String GROUP_ATTRIBUTE = "group";
    private static final String INTERFACE_ATTRIBUTE = "interface";
    private static final String ROUTINGKEY_ATTRIBUTE = "routingKey";
    private final static String ADDRESS_ATTRIBUTE = "addresses";
    private final static String SELECTOR_ATTRIBUTE = "selector";
    private final static String MAX_RECONSUME_TIMES_ATTRIBUTE = "maxReconsumeTimes";
    private final static String PULL_THRESHOLD_TOPIC_ATTRIBUTE = "pullThresholdForTopic";
    private final static String PULL_THRESHOLDSIZE_TOPIC_ATTRIBUTE = "pullThresholdSizeForTopic";

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ListenerFactoryBean.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {


        AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();
        beanDefinition.setBeanClass(ListenerFactoryBean.class);


        //
        String messageModel = element.getAttribute(MESSAGE_MODEL_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(messageModel)) {
            messageModel = Constants.MESSAGE_MODEL_CLUSTERING;
        }
        builder.addPropertyValue("messageModel", messageModel);

        String group = element.getAttribute(GROUP_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(messageModel)) {
            throw new BulletException("<bullet:listener-container> '" + GROUP_ATTRIBUTE + "' can't be blank");
        }
        builder.addPropertyValue(GROUP_ATTRIBUTE, group);

        String address = element.getAttribute(ADDRESS_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(address)) {
            throw new BulletException("<bullet:listener-container> " + ADDRESS_ATTRIBUTE + " attribute can't be blank");
        }
        builder.addPropertyValue(ADDRESS_ATTRIBUTE, address);

        String maxReconsumeTimes = element.getAttribute(MAX_RECONSUME_TIMES_ATTRIBUTE);
        if (StringUtils.isNoneEmpty(maxReconsumeTimes)) {
            builder.addPropertyValue(MAX_RECONSUME_TIMES_ATTRIBUTE, maxReconsumeTimes);
        }

        String pullThresholdForTopic = element.getAttribute(PULL_THRESHOLD_TOPIC_ATTRIBUTE);
        if (!StringUtils.isAnyEmpty(pullThresholdForTopic)) {
            builder.addPropertyValue(PULL_THRESHOLD_TOPIC_ATTRIBUTE, pullThresholdForTopic);
        }
        String pullThresholdSizeForTopic = element.getAttribute(PULL_THRESHOLDSIZE_TOPIC_ATTRIBUTE);
        if (!StringUtils.isAnyEmpty(pullThresholdSizeForTopic)) {
            builder.addPropertyValue(PULL_THRESHOLDSIZE_TOPIC_ATTRIBUTE, pullThresholdSizeForTopic);
        }

        NodeList childNodes = element.getChildNodes();
        ManagedList<Object> listeners = new ManagedList(childNodes.getLength());
        listeners.setSource(parserContext.extractSource(element));
        listeners.setMergeEnabled(Boolean.TRUE);
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String localName = parserContext.getDelegate().getLocalName(child);
                if (LISTENER_ELEMENT.equals(localName)) {
                    listeners.add(parseListener((Element) child, element, parserContext));
                }
            }
        }
        beanDefinition.getPropertyValues().add("messageListener", listeners);
    }

    private BeanDefinition parseListener(Element listenerEle, Element containerEle, ParserContext parserContext) {

        RootBeanDefinition listenerDef = new RootBeanDefinition(MessageListenerAdapter.class);
        listenerDef.setSource(parserContext.extractSource(listenerEle));

        String interfaze = listenerEle.getAttribute(INTERFACE_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(interfaze)) {
            throw new BulletException("<bullet:listener> '" + INTERFACE_ATTRIBUTE + "' can't be blank");
        }
        try {
            Class.forName(interfaze);
        } catch (ClassNotFoundException e) {
            throw new BulletException(e);
        }
        listenerDef.getPropertyValues().add(INTERFACE_ATTRIBUTE, interfaze);

        String ref = listenerEle.getAttribute(REF_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(ref)) {
            parserContext.getReaderContext().error("Listener 'ref' attribute contains empty value.", listenerEle);
        } else {
            listenerDef.getPropertyValues().add("delegate", new RuntimeBeanReference(ref));
        }

        //topic 主题名称
        String destination = listenerEle.getAttribute(DESTINATION_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(destination)) {
            parserContext.getReaderContext().error("Listener 'destination' attribute contains empty value.", listenerEle);
        } else {
            listenerDef.getPropertyValues().add(DESTINATION_ATTRIBUTE, destination);
        }

        String routingKey = listenerEle.getAttribute(ROUTINGKEY_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(routingKey)) {
            routingKey = "*";
        } else {
            listenerDef.getPropertyValues().add(ROUTINGKEY_ATTRIBUTE, routingKey);
        }

        String selector = listenerEle.getAttribute(SELECTOR_ATTRIBUTE);
        if (!StringUtils.isAnyEmpty(selector)) {
            listenerDef.getPropertyValues().add(SELECTOR_ATTRIBUTE, selector);
        }

        return listenerDef;
    }
}
