package com.kaicao.passivedns.entity;

import info.archinnov.achilles.annotations.*;

/**
 * Created by kaicao on 18/10/15.
 */
@Entity(keyspace = "passivedns", table = "lookup_record")
public class PassiveDNSLookupEntity {

    /**
     * DNS record types refer to:
     * https://en.wikipedia.org/wiki/List_of_DNS_record_types
     */
    public enum DNS_RECORD_TYPE {
        A // IPv4 address record
    }

    public enum LOOKUP_TYPE {QUERY, ANSWER}

    @CompoundPrimaryKey
    private PassiveDNSLookupID id;
    @Column
    private long firstSeenTimestamp;
    @Column
    private long lastSeenTimestamp;
    @Column
    private String query;
    @Column
    private String answer;

    public PassiveDNSLookupEntity() {
    }

    public PassiveDNSLookupEntity(String lookup, long pindex, DNS_RECORD_TYPE type, LOOKUP_TYPE lookupType, String result,
                                  long firstSeenTimestamp, long lastSeenTimestamp, String query, String answer) {
        this.id = new PassiveDNSLookupID(lookup, pindex, type, lookupType, result);
        this.firstSeenTimestamp = firstSeenTimestamp;
        this.lastSeenTimestamp = lastSeenTimestamp;
        this.query = query;
        this.answer = answer;
    }

    public static class PassiveDNSLookupID {
        @PartitionKey(value = 1)
        private String lookup;
        @PartitionKey(value = 2)
        private long pindex;
        @ClusteringColumn(value = 1)
        @Enumerated
        private DNS_RECORD_TYPE type;
        @ClusteringColumn(value = 2)
        @Enumerated
        private LOOKUP_TYPE lookupType;
        @ClusteringColumn(value = 3)
        private String result;

        public PassiveDNSLookupID() {
        }

        public PassiveDNSLookupID(String lookup, long pindex, DNS_RECORD_TYPE type, LOOKUP_TYPE lookupType, String result) {
            this.lookup = lookup;
            this.pindex = pindex;
            this.type = type;
            this.lookupType = lookupType;
            this.result = result;
        }

        public String getLookup() {
            return lookup;
        }

        public void setLookup(String lookup) {
            this.lookup = lookup;
        }

        public long getPindex() {
            return pindex;
        }

        public void setPindex(long pindex) {
            this.pindex = pindex;
        }

        public DNS_RECORD_TYPE getType() {
            return type;
        }

        public void setType(DNS_RECORD_TYPE type) {
            this.type = type;
        }

        public LOOKUP_TYPE getLookupType() {
            return lookupType;
        }

        public void setLookupType(LOOKUP_TYPE lookupType) {
            this.lookupType = lookupType;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "PassiveDNSLookupID{" +
                    "lookup='" + lookup + '\'' +
                    ", pindex=" + pindex +
                    ", type=" + type +
                    ", lookupType=" + lookupType +
                    ", result='" + result + '\'' +
                    '}';
        }
    }

    public PassiveDNSLookupID getId() {
        return id;
    }

    public void setId(PassiveDNSLookupID id) {
        this.id = id;
    }

    public long getFirstSeenTimestamp() {
        return firstSeenTimestamp;
    }

    public void setFirstSeenTimestamp(long firstSeenTimestamp) {
        this.firstSeenTimestamp = firstSeenTimestamp;
    }

    public long getLastSeenTimestamp() {
        return lastSeenTimestamp;
    }

    public void setLastSeenTimestamp(long lastSeenTimestamp) {
        this.lastSeenTimestamp = lastSeenTimestamp;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "PassiveDNSLookupEntity{" +
                "id=" + id +
                ", firstSeenTimestamp=" + firstSeenTimestamp +
                ", lastSeenTimestamp=" + lastSeenTimestamp +
                ", query='" + query + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
