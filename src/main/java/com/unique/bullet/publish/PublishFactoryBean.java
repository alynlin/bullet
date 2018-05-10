package com.unique.bullet.publish;

import com.unique.bullet.common.SendMode;
import lombok.Getter;
import lombok.Setter;
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

@Setter
@Getter
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
    private String communicationMode = SendMode.SYNC;
    //消息延迟级别
    private int delayTimeLevel;
    //消息过滤标识
    private Map<String, String> filterProp;
    //异步发送的回调
    private SendCallback sendCallback;
    //是否启用注解过滤，默认不启用
    private boolean filterAnnotation = false;


    @Override
    public void afterPropertiesSet() throws Exception {
        String interfaceName = interfaze.getName();

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setOptimize(false);
        proxyFactory.addInterface(interfaze);
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

    @Override
    public Object getObject() throws Exception {
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaze;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
