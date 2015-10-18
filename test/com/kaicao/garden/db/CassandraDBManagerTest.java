package com.kaicao.garden.db;

import org.junit.Test;

/**
 * Created by kaicao on 03/02/15.
 */
public class CassandraDBManagerTest {

    @Test
    public void init() throws Exception {
        CassandraDBManager dbManager = new CassandraDBManager();
        dbManager.init();
    }
}
