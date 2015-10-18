package com.kaicao.fx.entity;

import info.archinnov.achilles.annotations.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by kaicao on 30/09/15.
 * Partition key consist currencyPair and hour since Epoch
 * EUR/USD,20150107 14:07:44.140,1.18165,1.18169
 *
 * Default constructor for entity and ID is mandatory,
 * also getters/setters for all fields (including ID fields) are mandatory
 */
@Entity(table="fx_tick_records")
public class FxTickRecordEntity {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss.SSS");

    @CompoundPrimaryKey
    private RecordKey id;
    @Column
    private double bid;
    @Column
    private double ask;
    @Column
    private double spread;

    public FxTickRecordEntity() {
    }

    public FxTickRecordEntity(String currencyPair, String dateTimeStr, double bid, double ask) throws Exception {
        DateTime dateTime = DATE_TIME_FORMATTER.parseDateTime(dateTimeStr);
        this.id = new RecordKey(currencyPair, dateTime);
        this.bid = bid;
        this.ask = ask;
        this.spread = ask - bid;
    }

    public final static class RecordKey {
        @PartitionKey(value = 1)
        private String currencyPair;
        @PartitionKey(value = 2)
        private long hourSinceEpochMills;  // Hours since Epoch in millis
        @ClusteringColumn
        private long timestamp;

        public RecordKey() {
        }

        public RecordKey(String currencyPair, DateTime dateTime) {
            this.currencyPair = currencyPair;
            this.timestamp = dateTime.getMillis();
            this.hourSinceEpochMills = dateTime.hourOfDay().roundFloorCopy().getMillis();
        }

        public String getCurrencyPair() {
            return currencyPair;
        }

        public long getHourSinceEpochMills() {
            return hourSinceEpochMills;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setCurrencyPair(String currencyPair) {
            this.currencyPair = currencyPair;
        }

        public void setHourSinceEpochMills(long hourSinceEpochMills) {
            this.hourSinceEpochMills = hourSinceEpochMills;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public RecordKey getId() {
        return id;
    }

    public String getCurrencyPair() {
        return id.getCurrencyPair();
    }

    public long getHourSinceEpochMills() {
        return id.getHourSinceEpochMills();
    }

    public long getTimestamp() {
        return id.getTimestamp();
    }

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }

    public double getSpread() {
        return spread;
    }

    public void setId(RecordKey id) {
        this.id = id;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public void setSpread(double spread) {
        this.spread = spread;
    }
}
