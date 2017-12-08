package com.unique.bullet.publish;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;

public class PublishFactoryBean implements FactoryBean, InitializingBean, BeanClassLoaderAware, ApplicationContextAware, PriorityOrdered {


    private ApplicationContext applicationContext;
    private ClassLoader classLoader;
    private int codec;
    private DefaultMQProducer connection;
    private Class<?> interfaze;
    private Object proxy;
    private String routingKey;
    private int ttl;
    private String destination;

    public void afterPropertiesSet() throws Exception {
        String interfaceName = interfaze.getName();

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setOptimize(false);
        proxyFactory.addInterface(getInterface());
        proxyFactory.addAdvice(new MethodInvokeAdvice(interfaceName, connection, getRoutingKey(), codec, getTtl(),getDestination()));
        proxy = proxyFactory.getProxy(classLoader);
    }

    public int getCodec() {
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

    public int getTtl() {
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

    public void setCodec(int codec) {
        this.codec = codec;
    }


    public void setInterface(Class<?> interfaze) {
        this.interfaze = interfaze;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
