package com.unique.bullet.listener;

import com.unique.bullet.common.Constants;
import com.unique.bullet.exception.BulletException;
import com.unique.bullet.message.MessageRequest;
import com.unique.bullet.serializer.ISerializer;
import com.unique.bullet.serializer.SerializerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MessageListenerAdapter implements MessageListener {

    private static final Logger logger = LogManager.getLogger(MessageListenerAdapter.class);

    private String id;
    private Class<?> interfaze;
    private Object delegate;
    private String destination;
    private String routingKey;
    private String selector = null;

    public MessageListenerAdapter() {
    }

    public MessageListenerAdapter(Class interfaze, Object delegate, String destination, String routingKey) {
        this.interfaze = interfaze;
        this.delegate = delegate;
        this.destination = destination;
        this.routingKey = routingKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class<?> getInterface() {
        return interfaze;
    }

    public void setInterface(Class<?> interfaze) {
        this.interfaze = interfaze;
    }

    public void setDelegate(Object delegate) {
        Assert.notNull(delegate, "Delegate must not be null");
        this.delegate = delegate;
    }

    /**
     * Return the target object to delegate message listening to.
     */
    protected Object getDelegate() {
        return this.delegate;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getSelector() {
        return selector;
    }

    public void onMessage(MessageExt msg) throws BulletException {
        if (logger.isDebugEnabled()) {
            logger.debug("receive message:{}:{}", msg.getTopic(), msg.getTags());
        }
        //验证消息是否已过期
        if (isTll(msg)) {
            if (logger.isDebugEnabled()) {
                logger.debug("msg is expired:", msg.toString());
            }
            logger.info("msg is expired:{}:{}", msg.getTopic(), msg.getMsgId());
            return;
        }
        //获取编码方式
        ISerializer serializer = SerializerFactory.getSerializer(msg.getUserProperty(Constants.CODEC_TAG));
        byte[] body = msg.getBody();
        MessageRequest request = serializer.deserialize(body);
        String messageId = request.getMessageId();
        long timestamp = request.getTimestamp();
        String interfaceName = request.getInterfaceName();
        String methodName = request.getMethodName();
        Object[] args = request.getArgs();
        Class<?>[] types = request.getTypes();

        try {
            Class<?> clazz = Class.forName(interfaceName);
            Method method = clazz.getMethod(methodName, types);
            method.invoke(delegate, args);
        } catch (ClassNotFoundException e) {
            logger.error("consume message exception, " + msg.toString(), e);
            throw new BulletException(e);
        } catch (NoSuchMethodException e) {
            logger.error("consume message exception, " + msg.toString(), e);
            throw new BulletException(e);
        } catch (IllegalAccessException e) {
            logger.error("consume message exception, " + msg.toString(), e);
            throw new BulletException(e);
        } catch (InvocationTargetException e) {
            logger.error("consume message exception, " + msg.toString(), e);
            throw new BulletException(e);
        }
    }

    /**
     * 验证消息是否过期
     *
     * @param msg 待消费消息
     * @return true 过期；false 有效
     */
    private boolean isTll(MessageExt msg) {
        long ttl = Long.parseLong(msg.getUserProperty(Constants.X_MESSAGE_TTL));
        if (ttl > 0) {
            long bornTime = msg.getBornTimestamp();
            //TODO 客户端时间需要与消息中间件时间一致
            long currentTime = System.currentTimeMillis();
            return currentTime - bornTime >= ttl * 1000;
        }
        return false;
    }
}
