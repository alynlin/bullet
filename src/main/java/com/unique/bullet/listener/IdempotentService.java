package com.unique.bullet.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IdempotentService {

    /*private static final Logger LOGGER = LogManager.getLogger(IdempotentService.class);

    private static IdempotentService instance = new IdempotentService();

    private IdempotentService() {
    }

    public static IdempotentService getInstance() {
        return instance;
    }


    private UniqueModuleService uniqueModuleService;

    public String getKey(String consumeGroup, String msgKeys) {
        return consumeGroup + ":" + msgKeys;
    }

    *//**
     * @param consumeGroup 消费组
     * @param msgKeys      消息业务唯一标识
     * @return false 重复消费
     *//*
    public boolean isUnique(String consumeGroup, String msgKeys) {
        return isUnique(getKey(consumeGroup, msgKeys));
    }

    public boolean isUnique(String consumeGroup, String msgKeys, int ttl, int level) {
        boolean flag;
        switch (level) {
            case 0:
                flag = isUniqueExt(consumeGroup, msgKeys, ttl);
                break;
            case 1:
                flag = isUnique(consumeGroup, msgKeys, ttl);
                break;
            default:
                flag = isUniqueExt(consumeGroup, msgKeys, ttl);
                break;
        }
        return flag;
    }

    public boolean isUnique(String consumeGroup, String msgKeys, int ttl) {
        if (ttl == 0) {
            return isUnique(getKey(consumeGroup, msgKeys));
        }
        return isUnique(getKey(consumeGroup, msgKeys), ttl);
    }

    public boolean isUniqueExt(String consumeGroup, String msgKeys, int ttl) {
        boolean flag = true;
        try {
            flag = isUnique(consumeGroup, msgKeys, ttl);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return flag;
    }

    public boolean isUnique(String key) {
        return uniqueModuleService.isUnique(key, Constants.MAX_TTL);
    }

    public boolean isUnique(String key, int ttl) {
        return uniqueModuleService.isUnique(key, ttl);
    }

    public void remove(String key) {

        uniqueModuleService.remove(key);
    }

    *//**
     * 删除重复
     *
     * @param consumeGroup 消费组
     * @param msgKeys      消息业务唯一标识
     *//*
    public void remove(String consumeGroup, String msgKeys) {
        remove(getKey(consumeGroup, msgKeys));
    }


    public void setUniqueModuleService(Object uniqueModuleService) {
        this.uniqueModuleService = (UniqueModuleService) uniqueModuleService;
    }*/

}
