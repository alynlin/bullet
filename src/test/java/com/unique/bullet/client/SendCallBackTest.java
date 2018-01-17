package com.unique.bullet.client;

import com.unique.bullet.publish.BulletSendCallBack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.client.producer.SendResult;

public class SendCallBackTest extends BulletSendCallBack {

    private static final Logger logger = LogManager.getLogger(SendCallBackTest.class);

    public void onSuccess(SendResult sendResult) {
        super.onSuccess(sendResult);
        if (logger.isDebugEnabled()) {
            logger.debug(sendResult.getMsgId());
        }
        logger.info("send message[msgId:{}] to topic[{}] success", sendResult.getMsgId(), sendResult.getMessageQueue().getTopic());
    }

    public void onException(Throwable e) {
        logger.error(e);
    }

}
