package com.kaicao.garden.db;

import com.mongodb.BasicDBObject;

/**
 * Created by kaicao on 26/10/14.
 */
public class QueryRange<T> {
    private boolean includeMin;
    private boolean includeMax;
    private T min;
    private T max;

    public QueryRange(T min, boolean includeMin, T max, boolean includeMax) {
        this.includeMin = includeMin;
        this.includeMax = includeMax;
        this.min = min;
        this.max = max;
    }

    public boolean isIncludeMin() {
        return includeMin;
    }

    public boolean isIncludeMax() {
        return includeMax;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public BasicDBObject toQuery() {
        BasicDBObject query = new BasicDBObject();
        if (null != min) {
            query.put(includeMin ? "$gte" : "$gt", min);
        }
        if (null != max) {
            query.put(includeMax ? "$lte" : "$lt", max);
        }
        return query;
    }
}
