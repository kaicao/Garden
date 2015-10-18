package com.kaicao.passivedns.entity;

import info.archinnov.achilles.annotations.Column;
import info.archinnov.achilles.annotations.Entity;
import info.archinnov.achilles.annotations.PartitionKey;
import info.archinnov.achilles.type.Counter;

/**
 * Created by kaicao on 18/10/15.
 * Keep track of how many records exist for each lookup
 *
 * The technical limitations on Counter type are bound to their current implementation in Cassandra, namely:

 - it is not possible to set a TTL on a counter value
 - it is not possible to set a counter value to an arbitrary value.
 Only increment and decrement operations are allowed
 - it is not possible to delete/reset a counter value.
 However, it is possible to delete the entire counter column, though it is not really recommended.
 While removing a counter column, you may get non null value
 if you performs a read immediately after the removal
 */
@Entity(keyspace = "passivedns", table = "lookup_counter")
public class PassiveDNSCounterEntity {

    @PartitionKey
    private String lookup;
    @Column
    private Counter appear;

    public PassiveDNSCounterEntity() {
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public Counter getAppear() {
        return appear;
    }

    /* it is not sensible to invoke setter on a Counter field on a managed entity. You'll got an Exception anyway
        but setter is needed for Archilles to startup
     */
    public void setAppear(Counter appear) {
        this.appear = appear;
    }

}
