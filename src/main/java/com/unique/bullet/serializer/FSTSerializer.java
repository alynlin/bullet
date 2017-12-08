package com.unique.bullet.serializer;

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
    public byte[] serialize(Serializable obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return fst.asByteArray(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes) throws Exception {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        return (T) fst.asObject(bytes);
    }
}
