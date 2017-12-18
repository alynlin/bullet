package com.unique.bullet.publish;

import com.unique.bullet.common.SendMode;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;

import java.util.Map;

public class PublishFactoryBean implements FactoryBean, InitializingBean, BeanClassLoaderAware, ApplicationContextAware, PriorityOrdered {


    private ApplicationContext applicationContext;
    private ClassLoader classLoader;
    private String codec;
    private DefaultMQProducer connection;
    private Class<?> interfaze;
    private Object proxy;
    private String routingKey;
    private long ttl = -1;
    private String destination;
    private String communicationMode;
    private int delayTimeLevel;
    private Map<String, String> filterProp;
    private SendCallback sendCallback;

    public void afterPropertiesSet() throws Exception {
        String interfaceName = interfaze.getName();

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setOptimize(false);
        proxyFactory.addInterface(getInterface());
        MethodInvokeAdvice advice = new MethodInvokeAdvice(interfaceName, connection, getRoutingKey(), codec, getTtl(), getDestination());
        advice.setCommunicationMode(communicationMode);
        advice.setDelayTimeLevel(delayTimeLevel);
        advice.setFilterProp(filterProp);
        if (SendMode.ASYNC.equals(communicationMode)) {
            advice.setSendCallback(sendCallback == null ? new BulletSendCallBack() : sendCallback);
        }
        proxyFactory.addAdvice(advice);
        proxy = proxyFactory.getProxy(classLoader);
    }

    public String getCodec() {
        return codec;
    }


    public Class<?> getInterface() {
        return interfaze;
    }

    public Object getObject() throws Exception {
        return proxy;
    }

    public Class<?> getObjectType() {
        return interfaze;
    }

    public int getOrder() {
        return 0;
    }

    public DefaultMQProducer getConnection() {
        return connection;
    }

    public void setConnection(DefaultMQProducer connection) {
        this.connection = connection;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public long getTtl() {
        return ttl;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }


    public void setInterface(Class<?> interfaze) {
        this.interfaze = interfaze;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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
}
