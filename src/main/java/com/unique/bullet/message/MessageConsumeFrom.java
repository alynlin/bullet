package com.unique.bullet.message;

import com.unique.bullet.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * class_name: MessageConsumeFrom
 * describe: 起始消费位置
 * creat_user: unique
 * creat_date: 2018/3/15
 * creat_time: 10:40
 **/
public class MessageConsumeFrom {

    public static ConsumeFromWhere getConsumeFromWhere(String model) {

        return getConsumeFromWhere(model, null);
    }

    public static ConsumeFromWhere getConsumeFromWhere(String model, MessageModel messageModel) {

        if (StringUtils.isAnyEmpty(model)) {
            return defaultConsumeFromWhere(messageModel);
        }

        switch (model) {
            case Constants.CONSUME_FROM_FIRST_OFFSET:
                return ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET;
            case Constants.CONSUME_FROM_LAST_OFFSET:
                return ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;
            default:
                return ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET;
        }
    }

    /**
     * 根据消费类型，设置默认的起始消费位置
     *
     * @param messageModel 消费类型
     * @return 起始消费位置, 默认 集群模式使用FIRST_OFFSET，广播模式使用LAST_OFFSET
     */
    private static ConsumeFromWhere defaultConsumeFromWhere(MessageModel messageModel) {

        if (messageModel == MessageModel.CLUSTERING) {
            return ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET;
        }
        if (messageModel == MessageModel.BROADCASTING) {
            return ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;
        }
        return ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET;
    }

}
