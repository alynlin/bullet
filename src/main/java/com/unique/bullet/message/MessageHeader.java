package com.unique.bullet.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MessageHeader implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String,Object> attachments;
	private String messageId;
	private String resource;
	private long timestamp;

	public MessageHeader() {
		timestamp = System.currentTimeMillis();
		attachments = new HashMap<String,Object>();
	}
	public void addAttachment(String key,Object value) {
		attachments.put(key, value);
	}

	public void getAttachment(String key) {
		attachments.get(key);
	}

	public String getMessageId() {
		return messageId;
	}
	public String getResource() {
		return resource;
	}
	public long getTimestamp() {
		return timestamp;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
