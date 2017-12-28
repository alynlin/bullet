package com.unique.bullet.listener;

import com.unique.bullet.exception.BulletException;
import com.unique.bullet.message.MessageModelFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class ListenerFactoryBean implements InitializingBean, DisposableBean {

    private static final Logger logger = LogManager.getLogger(ListenerFactoryBean.class);
    private String codec;
    private DefaultMQPushConsumer consumer;
    private Class<?> interfaze;
    private String messageModel;
    private List<MessageListenerAdapter> messageListener;
    private String group;
    private String addresses;
    private int maxReconsumeTimes = 2;
    //针对topic 的流控,默认最大缓存1000
    private int pullThresholdForTopic = 1000;
    //针对topic的流控，默认最大100M
    private int pullThresholdSizeForTopic = 100;

    public void afterPropertiesSet() throws Exception {
        logger.info("rocketMQ consumer init:{}", group);
        initConsumer();
    }

    private void initConsumer() throws MQClientException {
        consumer = new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(addresses);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //消费类型（集群消费、广播消费）
        consumer.setMessageModel(MessageModelFactory.getMessageModel(messageModel));
        //重试次数
        consumer.setMaxReconsumeTimes(maxReconsumeTimes);
        //针对topic 的流控
        consumer.setPullThresholdForTopic(pullThresholdForTopic);
//      //针对topic的流控，基于消息大小
        consumer.setPullThresholdSizeForTopic(pullThresholdSizeForTopic);

        subscribe(consumer);
        //注册监听器
        registerMessageListener(consumer);
        //启动consumer
        consumer.start();
    }

    /**
     * 订阅topic
     * 过滤规则：优先使用tag进行过滤
     *
     * @param consumer 消费者
     * @throws MQClientException
     */
    private void subscribe(MQPushConsumer consumer) throws MQClientException {
        //订阅topic
        for (MessageListenerAdapter listener : messageListener) {
            //优先使用tag 过滤
            if (StringUtils.isAnyEmpty(listener.getSelector()) || StringUtils.isNoneEmpty(listener.getRoutingKey())) {
                consumer.subscribe(listener.getDestination(), listener.getRoutingKey());
                logger.info("订阅{}成功，过滤方式：routingKey：{}",listener.getDestination(),listener.getRoutingKey());
            } else {
                consumer.subscribe(listener.getDestination(), MessageSelector.bySql(listener.getSelector()));
                logger.info("订阅{}成功，过滤方式：selector：{}",listener.getDestination(),listener.getSelector());
            }
        }
    }

    /**
     * 根据消息topic 获取对应的listener
     *
     * @param msg
     * @return
     */
    private MessageListener getMessageBean(MessageExt msg) {
        for (MessageListenerAdapter listener : messageListener) {
            if (msg.getTopic().equals(listener.getDestination())) {
                //TODO listener的destination不能重复，默认返回第一个
                return listener;
            }
        }
        return null;
    }

    /**
     * 注册消息监听器
     *
     * @param consumer 消费者
     */
    private void registerMessageListener(MQPushConsumer consumer) {
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    //消费消息
                    try {
                        getMessageBean(msg).onMessage(msg);
                    } catch (BulletException e) {
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
    }


    public String getCodec() {
        return codec;
    }


    public Class<?> getInterface() {
        return interfaze;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public void setInterface(Class<?> interfaze) {
        this.interfaze = interfaze;
    }

    public String getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(String messageModel) {
        this.messageModel = messageModel;
    }

    public List<MessageListenerAdapter> getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(List<MessageListenerAdapter> messageListener) {
        this.messageListener = messageListener;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public void setMaxReconsumeTimes(int maxReconsumeTimes) {
        this.maxReconsumeTimes = maxReconsumeTimes;
    }

    public void setPullThresholdForTopic(int pullThresholdForTopic) {
        this.pullThresholdForTopic = pullThresholdForTopic;
    }

    public void setPullThresholdSizeForTopic(int pullThresholdSizeForTopic) {
        this.pullThresholdSizeForTopic = pullThresholdSizeForTopic;
    }

    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            logger.info("rocketmq consumer shutdown!");
            consumer.shutdown();
        }
    }
}
