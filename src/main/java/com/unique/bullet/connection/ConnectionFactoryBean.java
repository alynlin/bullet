package com.unique.bullet.connection;

import com.unique.bullet.common.SystemConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.PriorityOrdered;


public class ConnectionFactoryBean implements InitializingBean, FactoryBean, PriorityOrdered, DisposableBean {
    private final static Logger logger = LogManager.getLogger(ConnectionFactoryBean.class);
    private String addresses;
    private DefaultMQProducer producer = null;
    private int connectionTimeout = 1000;
    private String password = "guest";
    private String username = "guest";
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

    public void afterPropertiesSet() throws Exception {
        logger.info("rocketmq producer init");
        System.setProperty(SystemConfig.COM_ROCKETMQ_REMOTING_CLIENT_ASYNC_SEMAPHORE_VALUE, Integer.toString(permits));
        System.setProperty(SystemConfig.COM_ROCKETMQ_REMOTING_CLIENT_ONEWAY_SEMAPHORE_VALUE, Integer.toString(permits));

        producer = new DefaultMQProducer(sysgroup);
        producer.setNamesrvAddr(addresses);
        producer.setSendMsgTimeout(sendMsgTimeout);

        producer.start();
        logger.info("rocketmq producer init success");
    }

    public String getAddresses() {
        return addresses;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }


    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    public String getPassword() {
        return password;
    }


    public String getUsername() {
        return username;
    }


    public Object getObject() throws Exception {
        return producer;
    }

    public Class<?> getObjectType() {
        return DefaultMQProducer.class;
    }

    public boolean isSingleton() {
        return true;
    }


    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSysgroup(String sysgroup) {
        this.sysgroup = sysgroup;
    }

    public void setSendMsgTimeout(int sendMsgTimeout) {
        this.sendMsgTimeout = sendMsgTimeout;
    }

    public void setPermits(int permits) {
        this.permits = permits;
    }

    @Override
    public void destroy() throws Exception {

        if (producer != null) {
            producer.shutdown();
        }

    }
}
