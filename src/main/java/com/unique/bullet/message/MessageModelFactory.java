package com.unique.bullet.message;

import com.unique.bullet.common.Constants;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

public class MessageModelFactory {

    public static MessageModel getMessageModel(String model) {
        //默认使用集群方式消费
        if (model == null) {
            return MessageModel.CLUSTERING;
        }

        switch (model) {
            //集群模式
            case Constants.MESSAGE_MODEL_CLUSTERING:
                return MessageModel.CLUSTERING;
            case Constants.MESSAGE_MODEL_BROADCASTING:
                //广播模式
                return MessageModel.BROADCASTING;
            default:
                //默认使用集群方式消费
                return MessageModel.CLUSTERING;
        }
    }

}
