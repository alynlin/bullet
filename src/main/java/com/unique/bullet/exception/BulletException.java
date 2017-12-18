package com.unique.bullet.exception;

/**
 * bullet exception
 */
public class BulletException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BulletException() {
        super();
    }

    public BulletException(Throwable e) {
        super(e);
    }

    public BulletException(String message) {
        super(message);
    }

    public BulletException(Throwable e, String message) {
        super(message, e);
    }

}
