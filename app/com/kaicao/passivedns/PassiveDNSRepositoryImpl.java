package com.kaicao.passivedns;

import com.datastax.driver.core.Cluster;
import com.kaicao.garden.utils.RepositoryException;
import com.kaicao.passivedns.entity.PassiveDNSCounterEntity;
import com.kaicao.passivedns.entity.PassiveDNSLookupEntity;
import info.archinnov.achilles.persistence.PersistenceManager;
import info.archinnov.achilles.persistence.PersistenceManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * Created by kaicao on 18/10/15.
 */
@Singleton
public class PassiveDNSRepositoryImpl implements PassiveDNSRepository {

    private static final Logger LOG = LoggerFactory.getLogger(PassiveDNSRepositoryImpl.class);
    private static final String PASSIVEDNS_KEYSPACE = "passivedns";
    /**
     * Bucket size to determine how many records can exist in 1 bucket identified by pindex,
     * if amount of records for the partition exceed, pindex has to increase by 1 and store to new partition,
     * 10 here for testing purposes
     *
     * Notes, to determine buckets size, few aspects may help:
     * 1. Hard limitation of 2 billion cells per partition, and partition has to be reside in ONE node  https://wiki.apache.org/cassandra/CassandraLimitations
     * 2. When partition size over 64MB, cassandra will compact on disk, and affect performance based on comment of http://grokbase.com/t/cassandra/user/145gd6jmek/number-of-rows-under-one-partition-key
     */
    private static final long PARTITION_BUCKET_SIZE = 10_000;

    private PersistenceManager persistenceManager;

    @Override
    public void init(Cluster cluster) throws RepositoryException {
        if (persistenceManager != null) {
            return;
        }
        LOG.info("Initialize cassandra persistence manager for " + PASSIVEDNS_KEYSPACE);
        PersistenceManagerFactory persistenceManagerFactory =
                PersistenceManagerFactory.PersistenceManagerFactoryBuilder
                        .builder(cluster)
                        .withEntityPackages("com.kaicao.passivedns.entity")
                        .withKeyspaceName(PASSIVEDNS_KEYSPACE)
                        .forceTableCreation(true)   // Auto create table base on https://github.com/doanduyhai/Achilles/wiki/DDL-Scripts-Generation
                        .build();
        persistenceManager = persistenceManagerFactory.createPersistenceManager();
        if (persistenceManager == null) {
            throw new IllegalStateException("Not able to initialize PersistenceManager");
        }
        LOG.info("Cassandra persistence manager initialized for " + PASSIVEDNS_KEYSPACE);
    }

    /**
     * Result of local test environment
     * OS X 10.9.5, 2.7GHz Intel i5, 8G 1600MHz DDR3
     *
     * Single client thread tests:
     *
     * In pure insert, no update
     * Max around 1067.81 ops with PARTITION_BUCKET_SIZE = 10_000
     * If without first query to get counter and always check pindex = 0, around 1400 ops
     *
     * In 50-50 Insert-update scenario
     * around 500 ops
     *
     */
    @Override
    public PassiveDNSLookupEntity record1(PassiveDNSLookupAdd record) throws RepositoryException {
        String lookup = record.getLookup();
        PassiveDNSCounterEntity counterEntity = persistenceManager.find(PassiveDNSCounterEntity.class, lookup);
        long appears = counterEntity == null ? 0l : counterEntity.getAppear().get();
        long maxPIndex = appears / PARTITION_BUCKET_SIZE;  // 10 entries per bucket
        PassiveDNSLookupEntity.PassiveDNSLookupID existLookupID = null;
        for (long currentIndex = 0; currentIndex <= maxPIndex; currentIndex ++) {
            PassiveDNSLookupEntity.PassiveDNSLookupID lookupID = new PassiveDNSLookupEntity.PassiveDNSLookupID(
                    lookup, currentIndex, record.getType(), record.getLookupType(), record.getResult());
            // TODO For exist check no need to fetch whole entity
            if (persistenceManager.find(PassiveDNSLookupEntity.class, lookupID) != null) {
                existLookupID = lookupID;
                break;
            }
        }
        if (existLookupID != null) {
            // Update lastSeen
            PassiveDNSLookupEntity existRecord = persistenceManager.forUpdate(PassiveDNSLookupEntity.class, existLookupID);
            existRecord.setLastSeenTimestamp(record.getTimestamp());
            existRecord.setQuery(record.getQuery());
            existRecord.setAnswer(record.getAnswer());
            persistenceManager.update(existRecord);
            return existRecord;
        } else {
            PassiveDNSLookupEntity newRecord = new PassiveDNSLookupEntity(
                    record.getLookup(), maxPIndex, record.getType(), record.getLookupType(), record.getResult(),
                    record.getTimestamp(), 0l, record.getQuery(), record.getAnswer()
            );
            persistenceManager.insert(newRecord);
            // Increase counter for lookup
            counterEntity = persistenceManager.forUpdate(PassiveDNSCounterEntity.class, lookup);
            counterEntity.getAppear().incr();
            persistenceManager.update(counterEntity);
            return newRecord;
        }
    }

    @Override
    public PassiveDNSLookupEntity record2(PassiveDNSLookupAdd record) throws RepositoryException {
        return null;
    }

}
