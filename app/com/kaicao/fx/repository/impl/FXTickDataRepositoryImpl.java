package com.kaicao.fx.repository.impl;

import com.datastax.driver.core.Cluster;
import com.kaicao.fx.entity.FxTickRecordEntity;
import com.kaicao.fx.repository.FXTickDataRepository;
import com.kaicao.garden.utils.RepositoryException;
import info.archinnov.achilles.persistence.PersistenceManager;
import info.archinnov.achilles.persistence.PersistenceManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * Created by kaicao on 30/09/15.
 */
@Singleton
public class FXTickDataRepositoryImpl implements FXTickDataRepository {

    private static final Logger LOG = LoggerFactory.getLogger(FXTickDataRepositoryImpl.class);
    private static final String FX_KEYSPACE = "fx";

    private PersistenceManager persistenceManager;

    @Override
    public void init(Cluster cluster) throws RepositoryException {
        if (persistenceManager != null) {
            return;
        }
        /*
         * Before connect to cluster, make sure Keyspace fx is created:
         * CREATE KEYSPACE fx WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
         */
        LOG.info("Initialize cassandra persistence manager");
        PersistenceManagerFactory persistenceManagerFactory =
                PersistenceManagerFactory.PersistenceManagerFactoryBuilder
                        .builder(cluster)
                        .withEntityPackages("com.kaicao.fx.entity")
                        .withKeyspaceName(FX_KEYSPACE)
                        .forceTableCreation(true)   // Auto create table base on https://github.com/doanduyhai/Achilles/wiki/DDL-Scripts-Generation
                        .build();
        persistenceManager = persistenceManagerFactory.createPersistenceManager();
        if (persistenceManager == null) {
            throw new IllegalStateException("Not able to initialize PersistenceManager");
        }
        LOG.info("Cassandra persistence manager initialized");
    }

    @Override
    public void saveFxTickRecord(FxTickRecordEntity entity) {
        persistenceManager.insert(entity);
    }
}
