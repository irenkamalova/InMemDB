package com.kamalova.impl;

import com.kamalova.Transaction;

public class TransactionImpl implements Transaction {
    private final String id;

    public TransactionImpl(String id) {
        this.id = id;
    }
}
