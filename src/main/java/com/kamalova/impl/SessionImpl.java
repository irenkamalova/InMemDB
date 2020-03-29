package com.kamalova.impl;

import com.kamalova.CrudRepository;
import com.kamalova.Session;
import com.kamalova.Transaction;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SessionImpl<T, R> implements Session<T, R> {
    private static Logger logger = LoggerFactory.getLogger(SessionImpl.class);

    private final CrudRepository<T, R> repository;
    private CrudRepository<T, R> snapshotRepository;
    //private final Map<Transaction, List<Operation>> transactionsLog = new ConcurrentHashMap<>();
    private final List<Operation> operations = new ArrayList<>();
    private Transaction transaction;

    public SessionImpl(CrudRepository<T, R> repository) {
        this.repository = repository;
    }

    public R set(T key, R value) {
        if (transaction == null) {
            return repository.set(key, value);
        } else {
            operations.add(new Operation(Method.PUT, key, value));
            return snapshotRepository.set(key, value);
        }
    }

    public R get(T key) {
        if (transaction == null) {
            return repository.get(key);
        } else {
            operations.add(new Operation(Method.GET, key, null));
            return snapshotRepository.get(key);
        }
    }

    public R delete(T key) {
        if (transaction == null) {
            return repository.delete(key);
        } else {
            operations.add(new Operation(Method.DELETE, key, null));
            return snapshotRepository.delete(key);
        }
    }

    @Override
    public CrudRepository<T, R> getSnapshot() {
        if (transaction == null) {
            return repository.getSnapshot();
        } else {
            return snapshotRepository.getSnapshot();
        }
    }

    @Override
    public Transaction beginTransaction() {
        if (transaction == null) {
            transaction = new TransactionImpl(String.valueOf(System.currentTimeMillis()));
            snapshotRepository = repository.getSnapshot();
        } else {
            logger.error("Transaction have already created");
        }
        return transaction;
    }

    @Override
    public Transaction rollbackTransaction() {
        operations.forEach(Operation::cancel);
        return null;
    }

    @Override
    public Transaction commitTransaction() {
        operations.forEach(Operation::apply);
        snapshotRepository = null;
        return transaction;
    }

    private class Operation {
        final Method method;
        final T key;
        final R value;
        final Stack<Pair<T, R>> addedValues = new Stack<>();
        final Stack<Pair<T, R>> deletedKeys = new Stack<>();

        private Operation(Method method, T key, R value) {
            this.method = method;
            this.key = key;
            this.value = value;
        }

        void apply() {
            switch (method) {
                case PUT:
                    addedValues.add(Pair.of(key, value));
                    repository.set(key, value);
                    break;
                case DELETE:
                    R deletedValue = repository.delete(key);
                    deletedKeys.add(Pair.of(key, deletedValue));
                    break;
            }
        }

        void cancel() {
            switch (method) {
                case PUT:
                    Pair<T, R> added = addedValues.pop();
                    repository.delete(added.getKey());
                    break;
                case DELETE:
                    Pair<T, R> deleted = deletedKeys.pop();
                    repository.set(deleted.getKey(), deleted.getValue());
                    break;
            }
        }
    }

    enum Method {
        PUT, GET, DELETE;
    }

}
