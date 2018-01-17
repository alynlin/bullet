package com.unique.bullet.serializer;

import com.unique.bullet.common.Constants;
import com.unique.bullet.serializer.hessian.HessianSerialize;

public final class SerializerFactory {

    public static ISerializer getSerializer(String codec) {

        ISerializer serializer;
        switch (codec) {
            case Constants.CODEC_FST:
                serializer = FSTSerializer.getInstance();
                break;
            case Constants.CODEC_JDK:
                serializer = JDKSerializer.getInstance();
                break;
            case Constants.CODEC_HESSIAN:
                serializer = HessianSerialize.getInstance();
                break;
            default:
                serializer = JDKSerializer.getInstance();
                break;
        }
        return serializer;
    }
}
