package com.unique.bullet.service;

import com.unique.bullet.annotation.MessageProperties;
import com.unique.bullet.annotation.MessageProperty;
import com.unique.bullet.dto.Person;

import java.util.List;

public interface IService {


    public String sayHello(String name);

    public int sayInt(int i);

    public void sayVoid();

    public TxnCtrl sayTxnCtrl(TxnCtrl txnCtrl);

    void sendPerson(Person person);

    void sendPerson(@MessageProperties({@MessageProperty(name = "userName", jxpath = "name"), @MessageProperty(name = "userAge", jxpath = "age")}) Person person, @MessageProperty(name = "pname") String name, List<Person> persons);

}
