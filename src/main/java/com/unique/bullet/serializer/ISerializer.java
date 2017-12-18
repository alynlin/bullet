package com.unique.bullet.serializer;

import com.unique.bullet.exception.BulletException;

import java.io.IOException;
import java.io.Serializable;

public interface ISerializer {
	public byte[] serialize(Serializable obj) throws BulletException;
	public <T> T deserialize(byte[] bytes) throws BulletException;
}
