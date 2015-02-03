package com.kaicao.garden.db;

import com.kaicao.garden.models.Garden;
import com.kaicao.garden.utils.DBManagerException;

import java.util.List;

/**
 * Created by kaicao on 03/02/15.
 */
public interface DBManager {
    public void init();
    public void insert(Garden garden) throws DBManagerException;
    public int insert(List<Garden> gardens) throws DBManagerException;
    public Garden findById(String id) throws DBManagerException;
    public List<Garden> queryValueRange(QueryRange<Integer> range) throws DBManagerException;
    public long count();
    public void close() throws DBManagerException;
}
