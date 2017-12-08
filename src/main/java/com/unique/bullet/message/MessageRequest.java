package com.unique.bullet.message;

import java.io.Serializable;
import java.util.UUID;

public class MessageRequest extends MessageHeader implements Serializable {

    private static final long serialVersionUID = 1L;
    private Object[] args;
    private String interfaceName;
    private String methodName;
    private Class<?>[] types;
    private boolean callback;

    public boolean isCallback() {
        return callback;
    }

    public void setCallback(boolean callback) {
        this.callback = callback;
    }

    public MessageRequest() {
        super();
        super.setMessageId(UUID.randomUUID().toString());
    }

    public Object[] getArgs() {
        return args;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

}
