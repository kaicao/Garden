package com.kaicao.fx.repository;

import com.datastax.driver.core.Cluster;
import com.kaicao.fx.entity.FxTickRecordEntity;
import com.kaicao.garden.utils.RepositoryException;

/**
 * Created by kaicao on 30/09/15.
 */
public interface FXTickDataRepository {
    void init(Cluster cluster) throws RepositoryException;
    void saveFxTickRecord(FxTickRecordEntity entity) throws RepositoryException;
}
