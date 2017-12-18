package com.unique.bullet.serializer;

import com.unique.bullet.exception.BulletException;

import java.io.*;

public class JDKSerializer implements ISerializer {

    private static class SingletonHolder {
        private static final JDKSerializer INSTANCE = new JDKSerializer();
    }

    private JDKSerializer() {
    }

    public static final JDKSerializer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public byte[] serialize(Serializable obj) throws BulletException {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = null;
        byte[] bytes = null;
        try {
            try {
                objectOut = new ObjectOutputStream(output);
                objectOut.writeObject(obj);
                bytes = output.toByteArray();
            } catch (IOException e) {
                throw new BulletException(e);
            }

        } finally {
            try {
                if (objectOut != null) {
                    objectOut.close();
                }
            } catch (Exception e) {
            } finally {
                objectOut = null;
            }
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
            } finally {
                output = null;
            }

        }
        return bytes;
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes) throws BulletException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ObjectInputStream objectIn = null;
        Object object = null;
        try {
            try {
                objectIn = new ObjectInputStream(input);
                object = objectIn.readObject();
            } catch (IOException e) {
                throw new BulletException(e);
            } catch (ClassNotFoundException e) {
                throw new BulletException(e);
            }

        } finally {
            try {
                if (objectIn != null) {
                    objectIn.close();
                }
            } catch (Exception e) {
            } finally {
                objectIn = null;
            }
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
            } finally {
                input = null;
            }
        }
        return (T) object;
    }
}

