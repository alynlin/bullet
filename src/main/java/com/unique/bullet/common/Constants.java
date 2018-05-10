package com.unique.bullet.common;

public class Constants {
    public final static int CPUS = Runtime.getRuntime().availableProcessors();
    public final static int CAPACITY = 5 * CPUS;
    public final static String CODEC_FST = "fst";
    public final static String CODEC_JDK = "jdk";
    public final static String CODEC_HESSIAN = "hessian";
    //消息头，编码方式
    public final static String CODEC_TAG = "x-message-codec";
    public final static int COREPOOLSIZE = 2;
    //默认编码方式
    public final static String DEFAULT_CODEC = "hessian";
    public final static String DEFAULT_PASSWORD = "guest";
    public final static String DEFAULT_ROUTINGKEY = "DEFAULT";
    public final static long DEFAULT_TTL = -1;
    public final static String DEFAULT_USERNAME = "guest";
    public final static int MAXIMUMCOREPOOLSIZE = 5 * CPUS;
    public final static int NEED_ZIP = 1024;
    public final static String PEER_TO_PEER_TAG = "x-message-forward";
    public final static String RESPONSE_SUFFIX = "#response";
    //
    public final static int TIMEOUT_MILLISECONDS = 30 * 1000;
    public final static int UPZIP = 0;
    //消息过期时间 秒
    public final static String X_MESSAGE_TTL = "x-message-ttl";
    public final static int ZIP = 1;
    public final static String ZIP_TAG = "x-message-zip";
    //消费方式 集群方式
    public final static String MESSAGE_MODEL_CLUSTERING = "CLUSTERING";
    //消费方式 广播方式
    public final static String MESSAGE_MODEL_BROADCASTING = "BROADCASTING";
    //过滤属性前缀，防止与保留字段冲突
    public final static String PROP_FREFIX = "bullet_";
    //用户属性保留字段，用于查询追踪消息
    public final static String MESSAGE_KEYS_NAME = "bullet_messageKeys";

    public final static String CONSUME_FROM_FIRST_OFFSET = "FIRST_OFFSET";

    public final static String CONSUME_FROM_LAST_OFFSET = "LAST_OFFSET";
    //用于dynatrace
    public final static String DYNATRACE_TAG_KEY = "DYNATRACE_TAG";

    public static String IS_TRACE_FLAG = "IS_TRACE";
}
