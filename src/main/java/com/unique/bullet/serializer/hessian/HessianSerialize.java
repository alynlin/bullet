package com.unique.bullet.serializer.hessian;

import com.unique.bullet.exception.BulletException;
import com.unique.bullet.serializer.ISerializer;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class HessianSerialize implements ISerializer {

    private static class HessianHolder {
        private static final HessianSerialize INSTANCE = new HessianSerialize();
    }

    private HessianSerialize() {
    }

    public static final HessianSerialize getInstance() {
        return HessianSerialize.HessianHolder.INSTANCE;
    }

    @Override
    public byte[] serialize(Serializable obj) throws BulletException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(output);
        byte[] bytes = null;
        try {
            ho.startMessage();
            ho.writeObject(obj);
            ho.completeMessage();
            ho.close();
            output.close();
            bytes = output.toByteArray();
        } catch (IOException e) {
            throw new BulletException(e);
        } finally {
            try {
                if (ho != null) {
                    ho.close();
                }
            } catch (IOException e) {
            } finally {
                ho = null;
            }

            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
            } finally {
                output = null;
            }
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes) throws BulletException {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        Hessian2Input hi = new Hessian2Input(input);
        Object result = null;
        try {
            hi.startMessage();
            result = hi.readObject();
            hi.completeMessage();
        } catch (IOException e) {
            throw new BulletException(e);
        } finally {
            try {
                hi.close();
            } catch (IOException e) {
                hi = null;
            }

            try {
                input.close();
            } catch (IOException e) {
                input = null;
            }
        }
        return (T) result;
    }
}
