package com.unique.bullet.publish;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

public class BulletSendCallBack implements SendCallback {

    private static final Logger logger = LogManager.getLogger(BulletSendCallBack.class);

    @Override
    public void onSuccess(SendResult sendResult) {
        if (logger.isDebugEnabled()) {
            logger.debug(sendResult.getMsgId());
        }
    }

    @Override
    public void onException(Throwable e) {
        logger.error(e);
    }
}
