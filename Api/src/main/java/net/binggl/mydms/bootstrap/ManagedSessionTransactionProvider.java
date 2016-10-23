package net.binggl.mydms.bootstrap;

import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// https://gist.github.com/vvondra/1dbcd62306e40fa47294

/**
 * A helper class for running any database query within a transaction.
 *
 * `@UnitOfWork` annotation provided by the drop-wizard only works in a (End-point)Resource class.
 *
 * This uses ManagedSessionContext to check if this thread already has a transaction session
 * and re-uses it if needed
 */
public class ManagedSessionTransactionProvider implements TransactionProvider {

    private static final Logger logger = LoggerFactory.getLogger(ManagedSessionTransactionProvider.class);

    private final SessionFactory sessionFactory;

    public ManagedSessionTransactionProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <V> V transactional(Function<Session, V> callback) {
        return transactional(callback, false);
    }

    @Override
    public <V> V readOnlyTransactional(Function<Session, V> callback) {
        return transactional(callback, true);
    }

    private <V> V transactional(Function<Session, V> callback, boolean readOnly) {
        Session session = null;
        Transaction transaction;

        // We are already in a transaction
        if (ManagedSessionContext.hasBind(sessionFactory)) {
            if (sessionFactory.getCurrentSession().isDefaultReadOnly()) {
                throw new IllegalStateException("Cannot nest writable transaction in a read-only transaction");
            }

            return callback.apply(sessionFactory.getCurrentSession());
        }

        try {
            session = sessionFactory.openSession();
            ManagedSessionContext.bind(session);
            session.setDefaultReadOnly(readOnly);
            transaction = session.beginTransaction();
            try {
                V result = callback.apply(session);
                transaction.commit();
                return result;
            } catch (Exception e) {
                transaction.rollback();
                logger.error("Exception occurred while executing transaction", e);

                throw e;
            }
        } finally {
            if (session != null) {
                session.close();
            }

            ManagedSessionContext.unbind(sessionFactory);
        }
    }
}
