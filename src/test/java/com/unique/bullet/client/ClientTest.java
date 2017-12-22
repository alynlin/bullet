package com.unique.bullet.client;

import com.unique.bullet.dto.Car;
import com.unique.bullet.dto.Person;
import com.unique.bullet.service.IService;
import com.unique.bullet.service.PublishService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:client-rocket.xml"})
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class ClientTest {
    private final static Logger log = LoggerFactory.getLogger(ClientTest.class);

    @Autowired
    @Qualifier("service")
    IService service;

    @Resource
    PublishService publishService;

    @Resource
    IService serviceAnno;

    @Test
    public void testSayHello() throws InterruptedException {

//        while (true){
        service.sayHello("this is bullet msg");
        Thread.sleep(2);
//        }

    }

    @Test
    public void testSendPerson() {
        //service.sendPerson(PersonTest.getPerson());

        Person person = new Person();

        person.setAge(100);
        person.setName("unique");

        List<Car> cars = new LinkedList<>();
        Car car = new Car();
        car.setName("北京吉普");
        cars.add(car);

        person.setCars(cars);
        List<Person> persons = new ArrayList<Person>();
        persons.add(person);

        service.sendPerson(person,"unique",persons);
    }

    @Test
    public void testSendPersonAnno() {
        Person person = new Person();
        person.setAge(100);
        person.setName("unique");

        List<Car> cars = new LinkedList<>();
        Car car = new Car();
        car.setName("北京吉普");
        cars.add(car);

        person.setCars(cars);
        List<Person> persons = new ArrayList<Person>();
        persons.add(person);

        serviceAnno.sendPerson(person,"unique",persons);
    }


    @Test
    public void testPubHello() {

        publishService.sayHello("this is pub msg");

    }

}
