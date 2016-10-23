package net.binggl.mydms.bootstrap;

import java.util.function.Function;

import org.hibernate.Session;

public interface TransactionProvider {

    <V> V transactional(Function<Session, V> callback);

    <V> V readOnlyTransactional(Function<Session, V> callback);
}
