package com.unique.bullet.config;

import com.unique.bullet.common.Constants;
import com.unique.bullet.exception.BulletException;
import com.unique.bullet.listener.ListenerFactoryBean;
import com.unique.bullet.listener.MessageListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
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
    private final static String NAMESRV_DOMAIN_ELEMENT = "namesrv-domain";
    private final static String NAMESRV_DOMAIN_SUBGROUP_ELEMENT = "namesrv-domain-subgroup";
    private final static String CONSUME_FROM_WHERE = "consume-from-where";
    private final static String IDEMPOTENT_SERVICE = "idempotent-service";
    private final static String IDEMPOTENT_TTL = "idempotent-ttl";
    private final static String IDEMPOTENT_LEVEL = "idempotent-level";

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

        String idempotentService = element.getAttribute(IDEMPOTENT_SERVICE);
        if (StringUtils.isNoneEmpty(idempotentService)) {
            builder.addPropertyValue("idempotentService", new RuntimeBeanReference(idempotentService));
        }

        String idempotentTTL = element.getAttribute(IDEMPOTENT_TTL);
        if (StringUtils.isNoneEmpty(idempotentTTL)) {
            builder.addPropertyValue("idempotentTTL", idempotentTTL);
        }

        String idempotentLevel = element.getAttribute(IDEMPOTENT_LEVEL);
        if (StringUtils.isNoneEmpty(idempotentLevel)) {
            builder.addPropertyValue("idempotentLevel", idempotentLevel);
        }

        String address = element.getAttribute(ADDRESS_ATTRIBUTE);
        if (StringUtils.isNoneEmpty(address)) {
            builder.addPropertyValue(ADDRESS_ATTRIBUTE, address);
        }

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
        String namesrvDdomain = element.getAttribute(NAMESRV_DOMAIN_ELEMENT);
        if (StringUtils.isNoneEmpty(namesrvDdomain)) {
            builder.addPropertyValue("namesrvDdomain", new TypedStringValue(namesrvDdomain));
        }
        String namesrvDomainSubgroup = element.getAttribute(NAMESRV_DOMAIN_SUBGROUP_ELEMENT);
        if (StringUtils.isNoneEmpty(namesrvDomainSubgroup)) {
            builder.addPropertyValue("namesrvDomainSubgroup", new TypedStringValue(namesrvDomainSubgroup));
        }

        String consumeFromWhere = element.getAttribute(CONSUME_FROM_WHERE);
        builder.addPropertyValue("consumeFromWhere", consumeFromWhere);

        NodeList childNodes = element.getChildNodes();
        ManagedList listeners = new ManagedList(childNodes.getLength());
        listeners.setSource(parserContext.extractSource(element));
        listeners.setMergeEnabled(Boolean.TRUE);
        int listenerSize = 1;
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String localName = child.getLocalName();
                if (LISTENER_ELEMENT.equals(localName)) {
                    if (listenerSize > 1) {
                        //限制消费组下，只能订阅一个topic
                        throw new BulletException("<bullet:listener-container> can only contain one listener");
                    }
                    listeners.add(parseListener((Element) child, element, parserContext));
                    listenerSize++;
                }
            }
        }
        beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue("messageListener", listeners));
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
        listenerDef.getPropertyValues().addPropertyValue(new PropertyValue(INTERFACE_ATTRIBUTE, interfaze));

        String ref = listenerEle.getAttribute(REF_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(ref)) {
            parserContext.getReaderContext().error("Listener 'ref' attribute contains empty value.", listenerEle);
        } else {
            listenerDef.getPropertyValues().addPropertyValue(new PropertyValue("delegate", new RuntimeBeanReference(ref)));
        }

        //topic 主题名称
        String destination = listenerEle.getAttribute(DESTINATION_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(destination)) {
            parserContext.getReaderContext().error("Listener 'destination' attribute contains empty value.", listenerEle);
        } else {
            listenerDef.getPropertyValues().addPropertyValue(new PropertyValue(DESTINATION_ATTRIBUTE, destination));
        }

        String routingKey = listenerEle.getAttribute(ROUTINGKEY_ATTRIBUTE);
        if (StringUtils.isAnyEmpty(routingKey)) {
            routingKey = "*";
        } else {
            listenerDef.getPropertyValues().addPropertyValue(new PropertyValue(ROUTINGKEY_ATTRIBUTE, routingKey));
        }

        String selector = listenerEle.getAttribute(SELECTOR_ATTRIBUTE);
        if (!StringUtils.isAnyEmpty(selector)) {
            listenerDef.getPropertyValues().addPropertyValue(new PropertyValue(SELECTOR_ATTRIBUTE, selector));
        }

        return listenerDef;
    }
}
