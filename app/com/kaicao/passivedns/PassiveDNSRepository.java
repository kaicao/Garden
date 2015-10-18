package com.kaicao.passivedns;

import com.datastax.driver.core.Cluster;
import com.kaicao.garden.utils.RepositoryException;
import com.kaicao.passivedns.entity.PassiveDNSLookupEntity;

/**
 * Created by kaicao on 18/10/15.
 * Aim to test
 * 1. Unknown size partitioning. Using pindex as part of partition key and counter (count entries exist in partition)
 * to gather statistical information regarding partition size, and create new partition if neccessary
 *
 * 2. Situation where read-before-write is needed, e.g. need to determine whether to use
 * firstseen (if no record exist) or lastseen (if record exist) to store timestamp
 *
 */
public interface PassiveDNSRepository {

    void init(Cluster cluster) throws RepositoryException;
    /**
     * 1. Get current appears for lookup from PassiveDNSCounterEntity, to know how many pindex exists
     * 2. Select use pindex to find whether record exist
     * 3. If record exists -> update lastSeen
     * 3. If none succeeded -> insert new record, update PassiveDNSCounterEntity.appear
     *
     * Notes:
     * Inserting a null value creates a tombstone. Tombstones can have major performance implications.
     * You can see the tombstones using sstable2json.
     * https://github.com/doanduyhai/Achilles/wiki/Insert-Strategy
     * @param record
     * @return
     * @throws RepositoryException
     */
    PassiveDNSLookupEntity record1(PassiveDNSLookupAdd record) throws RepositoryException;

    PassiveDNSLookupEntity record2(PassiveDNSLookupAdd record) throws RepositoryException;
}
