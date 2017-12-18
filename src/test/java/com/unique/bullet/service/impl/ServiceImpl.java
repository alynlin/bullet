package com.unique.bullet.service.impl;


import com.unique.bullet.dto.Person;
import com.unique.bullet.service.IService;
import com.unique.bullet.service.TxnCtrl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ServiceImpl implements IService {

    private static final Logger logger = LogManager.getLogger(ServiceImpl.class);

    public String sayHello(String name) {

        logger.info("This message is from server,Hello " + name + "!");
        return "This message is from server,Hello " + name + "!";
    }

    public int sayInt(int i) {
        return i;
    }

    public TxnCtrl sayTxnCtrl(TxnCtrl txnCtrl) {
        return txnCtrl;
    }

    public void sendPerson(Person person) {

        logger.info(person.toString());

    }

    public void sendPerson(Person person, String name, List<Person> persons) {
        logger.info(person.toString());
        logger.info(name.toString());
        logger.info(persons.toString());
    }

    public void sayVoid() {

    }
}
