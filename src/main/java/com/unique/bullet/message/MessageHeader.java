package com.unique.bullet.message;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class MessageHeader implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Object> attachments;
    private String messageId;
    private String resource;
    private long timestamp;

    public MessageHeader() {
        timestamp = System.currentTimeMillis();
        attachments = new HashMap<String, Object>();
    }

    public void addAttachment(String key, Object value) {
        attachments.put(key, value);
    }

    public <T> T getAttachment(String key) {
        if (this.attachments == null || this.attachments.size() < 1) {
            return null;
        }
        return (T) this.attachments.get(key);
    }
}
