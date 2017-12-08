package com.unique.bullet.serializer;

import java.io.Serializable;

public interface ISerializer {
	public byte[] serialize(Serializable obj) throws Exception;
	public <T> T deserialize(byte[] bytes) throws Exception;
}
