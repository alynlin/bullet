package com.unique.bullet.service;

import com.unique.bullet.annotation.MessageProperties;
import com.unique.bullet.annotation.MessageProperty;
import com.unique.bullet.common.Constants;
import com.unique.bullet.dto.Person;

import java.util.List;

public interface IService {


    public String sayHello(String name);

    public int sayInt(int i);

    public void sayVoid();

    public TxnCtrl sayTxnCtrl(TxnCtrl txnCtrl);

    void sendPerson(Person person);

    void sendPerson(@MessageProperties({@MessageProperty(name = "bullet_userName", jxpath = "name"),
            @MessageProperty(name = "bullet_userAge", jxpath = "age"),@MessageProperty(name = Constants.MESSAGE_KEYS_NAME,jxpath = "id")}) Person person,
                    @MessageProperty(name = "bullet_pname") String name, List<Person> persons);

}
