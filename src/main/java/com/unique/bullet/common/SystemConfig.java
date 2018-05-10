package com.unique.bullet.common;

public class SystemConfig {

    public static final String COM_ROCKETMQ_REMOTING_SOCKET_SNDBUF_SIZE = //
            "com.rocketmq.remoting.socket.sndbuf.size";
    public static final String COM_ROCKETMQ_REMOTING_SOCKET_RCVBUF_SIZE = //
            "com.rocketmq.remoting.socket.rcvbuf.size";
    public static final String COM_ROCKETMQ_REMOTING_CLIENT_ASYNC_SEMAPHORE_VALUE = //
            "com.rocketmq.remoting.clientAsyncSemaphoreValue";
    public static final String COM_ROCKETMQ_REMOTING_CLIENT_ONEWAY_SEMAPHORE_VALUE = //
            "com.rocketmq.remoting.clientOnewaySemaphoreValue";

    public static final String ROCKETMQ_CLIENT_LOGLEVEL = "rocketmq.client.logLevel";
    //轮询从NameServer获取路由信息的时间间隔
    public static int POLL_NAMESERVER_INTERVAL = 10000;

}
