package com.unique.bullet.listener;

import com.unique.bullet.common.SystemConfig;
import com.unique.bullet.exception.BulletException;
import com.unique.bullet.message.MessageConsumeFrom;
import com.unique.bullet.message.MessageModelFactory;
import lombok.Getter;
import lombok.Setter;
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
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

@Getter
@Setter
public class ListenerFactoryBean implements InitializingBean, DisposableBean {

    private static final Logger logger = LogManager.getLogger(ListenerFactoryBean.class);
    private String codec;
    private DefaultMQPushConsumer consumer;
    private Class<?> interfaze;
    private String messageModel;
    private List<MessageListenerAdapter> messageListener;
    private String group;
    //消息去重服务
    private Object idempotentService;
    //消息去重有效期,单位秒
    private int idempotentTTL;
    private int idempotentLevel;
    private String addresses;
    private int maxReconsumeTimes = 0;
    //针对topic 的流控,默认最大缓存1000
    private int pullThresholdForTopic = 1000;
    //针对topic的流控，默认最大100M
    private int pullThresholdSizeForTopic = 100;
    private String consumeFromWhere;
    private String namesrvDdomain;
    private String namesrvDomainSubgroup;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("rocketMQ consumer init:{}", group);
        System.setProperty("rocketmq.namesrv.domain", namesrvDdomain);
        System.setProperty("rocketmq.namesrv.domain.subgroup", namesrvDomainSubgroup);
        initConsumer();
        logger.info("rocketMQ consumer init:{} success", group);
    }

    private void initConsumer() throws MQClientException {
        consumer = new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(addresses);
        //消费类型（集群消费、广播消费）
        consumer.setMessageModel(MessageModelFactory.getMessageModel(messageModel));
        consumer.setConsumeFromWhere(MessageConsumeFrom.getConsumeFromWhere(consumeFromWhere, consumer.getMessageModel()));
        //重试次数
        consumer.setMaxReconsumeTimes(maxReconsumeTimes);
        //针对topic 的流控
        consumer.setPullThresholdForTopic(pullThresholdForTopic);
//      //针对topic的流控，基于消息大小
        consumer.setPullThresholdSizeForTopic(pullThresholdSizeForTopic);
        //轮询从NameServer获取路由信息的时间间隔
        consumer.setPollNameServerInterval(SystemConfig.POLL_NAMESERVER_INTERVAL);
        consumer.setVipChannelEnabled(false);
        //持久化消费进度的间隔
        consumer.setPersistConsumerOffsetInterval(2000);

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
                logger.info("订阅{}成功，过滤方式：routingKey：{}", listener.getDestination(), listener.getRoutingKey());
            } else {
                consumer.subscribe(listener.getDestination(), MessageSelector.bySql(listener.getSelector()));
                logger.info("订阅{}成功，过滤方式：selector：{}", listener.getDestination(), listener.getSelector());
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

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                IdempotentService uniqueService = null;
                for (MessageExt msg : msgs) {
                    //消费消息
                    String msgOffset = msg.getTopic() + ":" + msg.getQueueId() + ":" + msg.getQueueOffset();
                    try {
                        //消息去重
                        /*if (idempotentService != null) {
                            uniqueService = IdempotentService.getInstance();
                            uniqueService.setUniqueModuleService(idempotentService);
                            if (!uniqueService.isUnique(group, msg.getKeys(), idempotentTTL, idempotentLevel)) {
                                logger.warn("[{}] comsume message[msgKeys:{}] from topic[{}] repeated", group, msg.getKeys(), msgOffset);
                                continue;
                            }
                        }*/
                        logger.info("[{}] comsume message[msgKeys:{}] from topic[{}]", group, msg.getKeys(), msgOffset);
                        getMessageBean(msg).onMessage(msg);
                        logger.info("[{}] comsume message[msgKeys:{}] from topic[{}] success", group, msg.getKeys(), msgOffset);
                    } catch (BulletException e) {
                        logger.error("[{}] comsume message[msgKeys:{}] from topic[{}] error", group, msg.getKeys(), msgOffset);
                        /*if (idempotentService != null) {
                            uniqueService.remove(group, msg.getKeys());
                        }*/
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            logger.info("rocketmq consumer shutdown!");
            consumer.shutdown();
        }
    }
}
