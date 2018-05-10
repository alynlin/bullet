package com.unique.bullet.connection;

import com.unique.bullet.common.SystemConfig;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.PriorityOrdered;

@Setter
@Getter
public class ConnectionFactoryBean implements InitializingBean, FactoryBean, PriorityOrdered, DisposableBean {
    private final static Logger logger = LogManager.getLogger(ConnectionFactoryBean.class);
    private String addresses;
    private DefaultMQProducer producer = null;
    private int connectionTimeout = 1000;
    private String sysgroup = null;
    /**
     * Timeout for sending messages.
     */
    private int sendMsgTimeout = 3000;
    /**
     * max num 65535
     * 发送信号量，限流使用，防止应用OOM
     */
    private int permits = 10000;

    private String namesrvDdomain;
    private String namesrvDomainSubgroup;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("rocketmq producer init");
        System.setProperty(SystemConfig.COM_ROCKETMQ_REMOTING_CLIENT_ASYNC_SEMAPHORE_VALUE, Integer.toString(permits));
        System.setProperty(SystemConfig.COM_ROCKETMQ_REMOTING_CLIENT_ONEWAY_SEMAPHORE_VALUE, Integer.toString(permits));
        System.setProperty("rocketmq.namesrv.domain", namesrvDdomain);
        System.setProperty("rocketmq.namesrv.domain.subgroup", namesrvDomainSubgroup);

        producer = new DefaultMQProducer(sysgroup);
        producer.setNamesrvAddr(addresses);
        producer.setSendMsgTimeout(sendMsgTimeout);
        //轮询从NameServer获取路由信息的时间间隔
        producer.setPollNameServerInterval(SystemConfig.POLL_NAMESERVER_INTERVAL);
        producer.setVipChannelEnabled(false);

        producer.start();
        logger.info("rocketmq producer init success");
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Object getObject() throws Exception {
        return producer;
    }

    @Override
    public Class<?> getObjectType() {
        return DefaultMQProducer.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {

        if (producer != null) {
            logger.info("rocketmq producer shutdown!");
            producer.shutdown();
        }

    }
}
