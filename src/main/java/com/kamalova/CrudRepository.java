package com.kamalova;

public interface CrudRepository<T, R> {

    R set(T key, R value);

    R get(T key);

    R delete(T key);

    CrudRepository<T, R> getSnapshot();
}
