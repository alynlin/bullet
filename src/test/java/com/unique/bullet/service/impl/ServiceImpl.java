package com.unique.bullet.service.impl;


import com.unique.bullet.service.IService;
import com.unique.bullet.service.TxnCtrl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServiceImpl implements IService {

    private static final Logger logger = LogManager.getLogger(ServiceImpl.class);

    public String sayHello(String name) {

        return "This message is from server,Hello " + name + "!";
    }

    public int sayInt(int i) {
        return i;
    }

    public TxnCtrl sayTxnCtrl(TxnCtrl txnCtrl) {
        return txnCtrl;
    }

    public void sayVoid() {
        // TODO Auto-generated method stub

    }
}
