package com.kamalova;

public interface Session<T, R> extends CrudRepository<T, R> {

    Transaction beginTransaction();

    /**
     * rolls back the most recent transaction
     * @return
     */
    Transaction rollbackTransaction();

    /**
     * commits all data from all nested transactions
     * @return
     */
    Transaction commitTransaction();
}
