package com.kamalova.impl;

import com.kamalova.CrudRepository;
import com.kamalova.Session;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrencySessionTest {

    CrudRepository<Integer, String> crudRepository = new InMemoryCrudRepository<>();

    @org.junit.jupiter.api.Test
    void multiThreadTest() {

        List<Thread> threads = new ArrayList<>();
        threads.add(new Thread(this::transactionOne));
        threads.add(new Thread(this::transactionTwo));
        threads.add(new Thread(this::transactionThree));

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(crudRepository.get(0)); // different result form different runs
        System.out.println(crudRepository.get(1)); // different result form different runs
    }

    void transactionOne() {
        Session<Integer, String> session = new SessionImpl<>(crudRepository);
        session.set(0, "0");
        //assertEquals("0", session.get(0)); - could be failed without transaction

        session.beginTransaction();
        session.set(1, "1");
        assertEquals("1", session.get(1));
        session.commitTransaction();

        //assertEquals("1", crudRepository.get(1));  - don't know what transaction will be the first
    }

    void transactionTwo() {
        Session<Integer, String> session = new SessionImpl<>(crudRepository);
        session.set(0, "00");
        //assertEquals("00", session.get(0)); - could be failed without transaction

        session.beginTransaction();
        session.set(1, "11");
        assertEquals("11", session.get(1));
        session.commitTransaction();

        //assertEquals("11", crudRepository.get(1)); - don't know what transaction will be the first
    }

    void transactionThree() {
        Session<Integer, String> session = new SessionImpl<>(crudRepository);
        session.set(0, "000");
        //assertEquals("000", session.get(0)); - could be failed without transaction

        session.beginTransaction();
        session.set(1, "111");
        assertEquals("111", session.get(1));
        session.commitTransaction();

        //assertEquals("111", crudRepository.get(1)); - don't know what transaction will be the first
    }
}
