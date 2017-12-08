package com.unique.bullet.publish;

import com.unique.bullet.message.MessageRequest;
import com.unique.bullet.serializer.ISerializer;
import com.unique.bullet.serializer.SerializerFactory;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.lang.reflect.Method;

public class MethodInvokeAdvice implements MethodInterceptor {
    private final static Logger log = LogManager.getLogger(MethodInvokeAdvice.class);
    private String interfaceName;
    private DefaultMQProducer producer;
    private String routingKey;
    private int codec;
    private int ttl;
    private String destination;

    public MethodInvokeAdvice(String interfaceName, DefaultMQProducer producer, String routingKey, int codec, int ttl, String destination) {
        this.interfaceName = interfaceName;
        this.producer = producer;
        this.routingKey = routingKey;
        this.codec = codec;
        this.ttl = ttl;
        this.destination = destination;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object[] args = invocation.getArguments();
        Method method = invocation.getMethod();
        Class<?>[] types = method.getParameterTypes();

        MessageRequest request = new MessageRequest();
        request.setInterfaceName(interfaceName);
        request.setMethodName(method.getName());
        request.setArgs(args);
        request.setTypes(types);

        ISerializer serializer = SerializerFactory.getSerializer(codec);
        byte[] bytes = serializer.serialize(request);
        byte[] body = null;

        body = bytes;

        Message msg = new Message(destination /* Topic */, routingKey /* Tag */, body /* Message body */);

        producer.send(msg);

        /*RabbitRequest request = new RabbitRequest();
        Object[] args = invocation.getArguments();
        Method method = invocation.getMethod();
        Class<?>[] types = method.getParameterTypes();
        //
        request.setInterfaceName(interfaceName);
        request.setMethodName(method.getName());
        request.setArgs(args);
        request.setTypes(types);
        ISerializer serializer = SerializerFactory.getSerializer(codec);
        byte[] bytes = serializer.serialize(request);
        byte[] body = null;
        Map<String, Object> headers = new HeadersBuilder().setBody(bytes).setCodec(codec).builder();
        if ((Integer) headers.get(Constants.ZIP_TAG) == Constants.ZIP) {
            body = QuickLZ.compress(bytes, 1);
            if (log.isDebugEnabled()) {
                log.debug(">>>>>{}/{}={},messageId={}", body.length, bytes.length, body.length / (double) bytes.length, request.getMessageId());
            }
        } else {
            body = bytes;
        }
        BasicProperties properties = new MessagePropertiesBuilder()
                .setDeliveryMode(2)
                .setPriority(0)
                .setHeaders(headers)
                .setExpiration(String.valueOf(ttl * 1000))
                .build();
        log.info(">>>>>publish invoke {} in remote server,messageId={}", new Object[]{TypesUtil.convert(interfaceName, method.getName(), types), request.getMessageId()});
        channel.basicPublish(interfaceName, routingKey, properties, body);
        Class<?> returnType = invocation.getMethod().getReturnType();
        return DefaultResult.asyncResult(returnType);*/

        return null;
    }
}
