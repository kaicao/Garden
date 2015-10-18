package com.kaicao.passivedns;

import com.kaicao.passivedns.entity.PassiveDNSLookupEntity;

/**
 * Created by kaicao on 18/10/15.
 */
public class PassiveDNSLookupAdd {
    private final String lookup;
    private final PassiveDNSLookupEntity.DNS_RECORD_TYPE type;
    private final PassiveDNSLookupEntity.LOOKUP_TYPE lookupType;
    private final String result;
    private final long timestamp;   // Timestamp of the record
    private final String query;
    private final String answer;

    public PassiveDNSLookupAdd(String lookup, PassiveDNSLookupEntity.DNS_RECORD_TYPE type, PassiveDNSLookupEntity.LOOKUP_TYPE lookupType,
                               String result, long timestamp, String query, String answer) {
        this.lookup = lookup;
        this.type = type;
        this.lookupType = lookupType;
        this.result = result;
        this.timestamp = timestamp;
        this.query = query;
        this.answer = answer;
    }

    public String getLookup() {
        return lookup;
    }

    public PassiveDNSLookupEntity.DNS_RECORD_TYPE getType() {
        return type;
    }

    public PassiveDNSLookupEntity.LOOKUP_TYPE getLookupType() {
        return lookupType;
    }

    public String getResult() {
        return result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getQuery() {
        return query;
    }

    public String getAnswer() {
        return answer;
    }
}
