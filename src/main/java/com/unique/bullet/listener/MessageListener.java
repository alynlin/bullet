package com.unique.bullet.listener;

import com.unique.bullet.exception.BulletException;
import org.apache.rocketmq.common.message.MessageExt;

public interface MessageListener {

    void onMessage(MessageExt msg) throws BulletException;

}
