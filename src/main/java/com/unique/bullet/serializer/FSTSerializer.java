package com.unique.bullet.serializer;

import com.unique.bullet.exception.BulletException;
import de.ruedigermoeller.serialization.FSTConfiguration;

import java.io.Serializable;

public class FSTSerializer implements ISerializer {
    private final static FSTConfiguration fst = FSTConfiguration.createDefaultConfiguration();

    private static class FSTHolder {
        private static final FSTSerializer INSTANCE = new FSTSerializer();
    }

    private FSTSerializer() {
    }

    public static final FSTSerializer getInstance() {
        return FSTSerializer.FSTHolder.INSTANCE;
    }

    @Override
    public byte[] serialize(Serializable obj) throws BulletException {
        if (obj == null) {
            return null;
        }
        try {
            return fst.asByteArray(obj);
        } catch (Throwable e) {
            throw new BulletException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes) throws BulletException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        try {
            return (T) fst.asObject(bytes);
        } catch (Throwable e) {
            throw new BulletException(e);
        }
    }
}
