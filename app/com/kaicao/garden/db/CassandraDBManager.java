package com.kaicao.garden.db;

import com.kaicao.garden.models.Garden;
import com.kaicao.garden.utils.DBManagerException;

import java.util.List;

/**
 * Created by kaicao on 03/02/15.
 */
public class CassandraDBManager implements DBManager {

    @Override
    public void init() {

    }

    @Override
    public void insert(Garden garden) throws DBManagerException {

    }

    @Override
    public int insert(List<Garden> gardens) throws DBManagerException {
        return 0;
    }

    @Override
    public Garden findById(String id) throws DBManagerException {
        return null;
    }

    @Override
    public List<Garden> queryValueRange(QueryRange<Integer> range) throws DBManagerException {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void close() throws DBManagerException {

    }
}
