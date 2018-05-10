package com.unique.bullet.publish;

import com.unique.bullet.annotation.support.HandlerMethodAnnoParser;
import com.unique.bullet.common.Constants;
import com.unique.bullet.common.SendMode;
import com.unique.bullet.common.Validators;
import com.unique.bullet.message.MessageRequest;
import com.unique.bullet.serializer.ISerializer;
import com.unique.bullet.serializer.SerializerFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.lang.reflect.Method;
import java.util.Map;

public class MethodInvokeAdvice implements MethodInterceptor {
    private final static Logger logger = LogManager.getLogger(MethodInvokeAdvice.class);
    private String interfaceName;
    private DefaultMQProducer producer;
    private String routingKey;
    private String codec;
    private long ttl;
    private String destination;
    private String communicationMode;
    private int delayTimeLevel;
    private Map<String, String> filterProp;
    private SendCallback sendCallback;
    //是否启用注解过滤
    private boolean filterAnnotation = false;

    /**
     * Timeout for sending messages.
     */
    public MethodInvokeAdvice(String interfaceName, DefaultMQProducer producer, String routingKey, String codec, long ttl, String destination) {
        this.interfaceName = interfaceName;
        this.producer = producer;
        this.routingKey = routingKey;
        this.codec = codec;
        this.ttl = ttl;
        this.destination = destination;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object[] args = invocation.getArguments();
        Method method = invocation.getMethod();
        Class<?>[] types = method.getParameterTypes();

        //包装请求
        MessageRequest request = new MessageRequest();
        //TODO 删除interfaceName
        request.setInterfaceName(interfaceName);
        request.setMethodName(method.getName());
        request.setArgs(args);
        request.setTypes(types);

        ISerializer serializer = SerializerFactory.getSerializer(codec);
        byte[] bytes = serializer.serialize(request);

        Message msg = new Message(destination /* Topic */, routingKey /* Tag */, bytes /* Message body */);
        //默认delayTimeLevel=0，消息不延迟
        msg.setDelayTimeLevel(delayTimeLevel);
        msg.putUserProperty(Constants.CODEC_TAG, codec);
        msg.putUserProperty(Constants.X_MESSAGE_TTL, Long.toString(ttl));
        if (filterProp != null) {
            Validators.checkFilterProp(filterProp);
            for (Map.Entry<String, String> entry : filterProp.entrySet()) {
                if (Constants.MESSAGE_KEYS_NAME.equals(entry.getKey())) {
                    msg.setKeys(entry.getValue());
                } else {
                    msg.putUserProperty(entry.getKey(), entry.getValue());
                }
            }
        }
        //注解方式过滤
        parseAnnotationFilterValue(invocation, msg);

        if (StringUtils.isAnyEmpty(msg.getKeys())) {
            msg.setKeys(request.getMessageId());
        }
        //发送消息
        return sendMessage(msg);
    }

    /**
     * 解析获取参数注解属性，用于消息过过滤标识
     *
     * @param invocation method interceptor
     * @param msg        to send
     */
    private void parseAnnotationFilterValue(MethodInvocation invocation, Message msg) {
        //启用注解过滤
        if (filterAnnotation) {
            Object[] args = invocation.getArguments();
            Method method = invocation.getMethod();

            Map<String, String> filterPropertyMap = HandlerMethodAnnoParser.parseMessageProperty(method, args);
            //添加并更新过滤标识
            if (filterPropertyMap != null) {
                Validators.checkFilterProp(filterPropertyMap);
                for (Map.Entry<String, String> entry : filterPropertyMap.entrySet()) {
                    if (Constants.MESSAGE_KEYS_NAME.equals(entry.getKey())) {
                        msg.setKeys(entry.getValue());
                    } else {
                        msg.putUserProperty(entry.getKey(), entry.getValue());

                    }
                }
            }
        }
    }

    /**
     * @param msg to send
     * @throws InterruptedException
     * @throws RemotingException
     * @throws MQClientException
     * @throws MQBrokerException
     */
    private boolean sendMessage(Message msg) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        boolean sendOk = true;
        switch (communicationMode) {
            case SendMode.ASYNC:
                //异步方式发送消息
                sendOk = asynSend(msg);
                break;
            case SendMode.ONEWAY:
                sendOk = sendOneway(msg);
                break;
            case SendMode.SYNC:
                //同步方式发送消息
                sendOk = send(msg);
                break;
            default:
                //默认使用异步方式
                sendOk = asynSend(msg);
                break;
        }
        return sendOk;
    }

    /**
     * 同步发送消息，不抛出异常，代表发送成功
     *
     * @param msg Message to send
     * @return 不抛出异常，代表发送成功
     * @throws InterruptedException
     * @throws RemotingException
     * @throws MQClientException
     * @throws MQBrokerException
     */
    private boolean send(Message msg) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        SendResult sendResult = producer.send(msg);
        logger.info("[{}] send message[msgKeys:{}] to topic[{}] success sendResult[{}]", producer.getProducerGroup(), msg.getKeys(), msg.getTopic(), sendResult.getSendStatus());
        return true;
    }

    /**
     * 异步发送
     *
     * @param msg Message to send
     * @throws InterruptedException
     * @throws RemotingException
     * @throws MQClientException
     * @throws MQBrokerException
     */
    private boolean asynSend(Message msg) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        producer.send(msg, sendCallback);
        logger.info("[{}] asynSend message[msgKeys:{}] to topic[{}] success", producer.getProducerGroup(), msg.getKeys(), msg.getTopic());
        return true;
    }


    /**
     * 此方法不等待broker返回ack，可能会有消息丢失
     *
     * @param msg Message to send
     * @throws RemotingException
     * @throws MQClientException
     * @throws InterruptedException
     */
    private boolean sendOneway(Message msg) throws RemotingException, MQClientException, InterruptedException {
        producer.sendOneway(msg);
        logger.info("[{}] sendOneway message[msgKeys:{}] to topic[{}] success", producer.getProducerGroup(), msg.getKeys(), msg.getTopic());
        return true;
    }

    public void setCommunicationMode(String communicationMode) {
        this.communicationMode = communicationMode;
    }

    public void setDelayTimeLevel(int delayTimeLevel) {
        this.delayTimeLevel = delayTimeLevel;
    }

    public void setFilterProp(Map<String, String> filterProp) {
        this.filterProp = filterProp;
    }

    public void setSendCallback(SendCallback sendCallback) {
        this.sendCallback = sendCallback;
    }

    public void setFilterAnnotation(boolean filterAnnotation) {
        this.filterAnnotation = filterAnnotation;
    }
}
