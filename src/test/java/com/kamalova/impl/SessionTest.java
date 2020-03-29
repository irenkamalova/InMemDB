package com.kamalova.impl;

import com.kamalova.CrudRepository;
import com.kamalova.Session;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @org.junit.jupiter.api.Test
    void testWorkFlow() {
        CrudRepository<Integer, String> crudRepository = new InMemoryCrudRepository<>();

        Session<Integer, String> session = new SessionImpl<>(crudRepository);

        session.set(0, "0");
        assertEquals("0", session.get(0));
        session.set(2, "2");

        session.beginTransaction();

        session.set(1, "1");
        assertEquals("1", session.get(1));

        session.set(0, "00");
        assertEquals("00", session.get(0));
        // original data shouldn't be changed:
        assertEquals("0", crudRepository.get(0));
        session.delete(2);

        // original data shouldn't be changed:
        assertEquals("2", crudRepository.get(2));

        session.commitTransaction();

        assertEquals("1", crudRepository.get(1));
        assertEquals("00", crudRepository.get(0));
        assertNull(crudRepository.get(2));

        session.rollbackTransaction();


    }
}