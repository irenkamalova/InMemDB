package com.kamalova.impl;

import com.kamalova.CrudRepository;
import java.util.concurrent.ConcurrentSkipListMap;

public class InMemoryCrudRepository<T, R> implements CrudRepository<T, R> {
    /**
     * ConcurrentSkipListMap providing expected average log(n) time cost for the containsKey, get, put and remove operations and their variants.
     * Insertion, removal, update, and access operations safely execute concurrently by multiple threads.
     */
    private final ConcurrentSkipListMap<T, R> map;

    public InMemoryCrudRepository() {
        this.map = new ConcurrentSkipListMap<T, R>();
    }

    public InMemoryCrudRepository(ConcurrentSkipListMap<T, R> map) {
        this.map = map;
    }

    public R set(T key, R value) {
        return map.put(key, value);
    }

    public R get(T key) {
        return map.get(key);
    }

    public R delete(T key) {
        return map.remove(key);
    }

    @Override
    public CrudRepository<T, R> getSnapshot() {
        return new InMemoryCrudRepository<T, R>(new ConcurrentSkipListMap<>(map));
    }
}
