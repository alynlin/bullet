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
    //编码方式
    private String codec;
    private DefaultMQProducer connection;
    //暴露服务接口
    private Class<?> interfaze;
    //服务接口代理对象
    private Object proxy;
    //message tag 过滤标识
    private String routingKey;
    //过期时间，单位秒
    private long ttl = -1;
    //消息地址，topic 名称
    private String destination;
    //交互方式，异步、同步、发送不确认
    private String communicationMode;
    //消息延迟级别
    private int delayTimeLevel;
    //消息过滤标识
    private Map<String, String> filterProp;
    //异步发送的回调
    private SendCallback sendCallback;
    //是否启用注解过滤，默认不启用
    private boolean filterAnnotation = false;


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
        advice.setFilterAnnotation(filterAnnotation);
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

    public void setFilterAnnotation(boolean filterAnnotation) {
        this.filterAnnotation = filterAnnotation;
    }
}
