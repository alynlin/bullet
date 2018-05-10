package com.unique.bullet.message;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
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
}
