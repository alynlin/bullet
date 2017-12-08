package com.unique.bullet.serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SerializerFactory {

    private static Map<String, ISerializer> serializerFactory;

    static {
        serializerFactory = new ConcurrentHashMap<String,ISerializer>();
        serializerFactory.put("jdk", JDKSerializer.getInstance());
        serializerFactory.put("fst", FSTSerializer.getInstance());
    }

    public static ISerializer getSerializer(int codec) {

        ISerializer serializer = serializerFactory.get(codec);
        if (serializer == null) {
            return JDKSerializer.getInstance();
        }
        return serializer;
    }
}
