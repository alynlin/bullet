package com.unique.bullet.client;

import com.unique.bullet.publish.BulletSendCallBack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.client.producer.SendResult;

public class SendCallBackTest extends BulletSendCallBack {

    private static final Logger logger = LogManager.getLogger(SendCallBackTest.class);

    public void onSuccess(SendResult sendResult) {
        if (logger.isDebugEnabled()) {
            logger.debug(sendResult.getMsgId());
        }
    }

    public void onException(Throwable e) {
        logger.error(e);
    }

}
