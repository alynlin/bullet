package com.unique.bullet.connection;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.PriorityOrdered;

public class ConnectionFactoryBean implements InitializingBean, FactoryBean, PriorityOrdered {
    private final static Logger log = LoggerFactory.getLogger(ConnectionFactoryBean.class);
    private String addresses;
    private boolean automaticRecoveryEnabled = true;
    private DefaultMQProducer producer = null;
    private int connectionTimeout = 1000;
    private int networkRecoveryInterval = 2000;
    private String password = "guest";
    private int requestedHeartbeat = 30;
    private boolean topologyRecoveryEnabled = true;
    private String username = "guest";
    private String sysgroup = null;

    public void afterPropertiesSet() throws Exception {

        producer = new DefaultMQProducer(sysgroup);
        producer.setNamesrvAddr(addresses);

        producer.start();
    }

    public String getAddresses() {
        return addresses;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getNetworkRecoveryInterval() {
        return networkRecoveryInterval;
    }

    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    public String getPassword() {
        return password;
    }

    public int getRequestedHeartbeat() {
        return requestedHeartbeat;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAutomaticRecoveryEnabled() {
        return automaticRecoveryEnabled;
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

    public boolean isTopologyRecoveryEnabled() {
        return topologyRecoveryEnabled;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public void setAutomaticRecoveryEnabled(boolean automaticRecoveryEnabled) {
        this.automaticRecoveryEnabled = automaticRecoveryEnabled;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setNetworkRecoveryInterval(int networkRecoveryInterval) {
        this.networkRecoveryInterval = networkRecoveryInterval;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRequestedHeartbeat(int requestedHeartbeat) {
        this.requestedHeartbeat = requestedHeartbeat;
    }

    public void setTopologyRecoveryEnabled(boolean topologyRecoveryEnabled) {
        this.topologyRecoveryEnabled = topologyRecoveryEnabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSysgroup(String sysgroup) {
        this.sysgroup = sysgroup;
    }
}
