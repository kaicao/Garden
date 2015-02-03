package com.kaicao.garden.db;

import com.kaicao.garden.models.Garden;
import com.kaicao.garden.utils.DBManagerException;
import com.mongodb.*;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by kaicao on 26/10/14.
 */
@Singleton
public class MongoDBManager implements DBManager {
    private final static String HOST = "ds047950.mongolab.com";
    private final static int PORT = 47950;
    private final static String USERNAME = "admin";
    private final static String PASSWORD = "admin";
    private final static String DATABASE = "kaicao";
    private static MongoClient client;
    private static DB db;

    public MongoDBManager() throws Exception {
        try {
            if (null == client && null == db) {
                client = new MongoClient(
                        new ServerAddress(HOST, PORT),
                        Arrays.asList(MongoCredential.createMongoCRCredential(USERNAME, DATABASE, PASSWORD.toCharArray()))
                );
                db = client.getDB(DATABASE);
            }
        } catch (Exception e) {
            System.err.println("Not able to init MongoClient " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void init() {
        // Cap 100 mega
        if (!getCollectionNames().contains("Garden")) {
            db.createCollection("Garden", (new BasicDBObject("capped", true).append("size", 104800000)));
        }
    }

    public Set<String> getCollectionNames() {
        return db.getCollectionNames();
    }

    @Override
    public void insert(Garden garden) throws DBManagerException {
        try {
            db.getCollection("Garden").save(garden);
        } catch (Exception e) {
            throw new DBManagerException("Fail to insert garden " + e.getMessage(), e);
        }
    }

    @Override
    public long count() {
        return db.getCollection("Garden").count();
    }

    @Override
    public Garden findById(String id) throws DBManagerException {
        try {
            BasicDBObject query = new BasicDBObject("id", id);
            DBCollection collection = db.getCollection("Garden");
            collection.setObjectClass(Garden.class);
            return (Garden) collection.find(query).next();
        } catch (Exception e) {
            throw new DBManagerException("Fail to findById " + e.getMessage(), e);
        }
    }

    @Override
    public List<Garden> queryValueRange(QueryRange<Integer> range) throws DBManagerException {
        try {
            DBCollection collection = db.getCollection("Garden");
            collection.setObjectClass(Garden.class);
            DBCursor cursor = collection.find(new BasicDBObject("value", range.toQuery()));
            List<Garden> result = new ArrayList<>();
            while (cursor.hasNext()) {
                result.add((Garden) cursor.next());
            }
            return result;
        } catch (Exception e) {
            throw new DBManagerException("Fail to queryValueRange " + e.getMessage(), e);
        }
    }

    @Override
    public void close() throws DBManagerException {
        try {
            db.getCollection("Garden").drop();
        } catch (Exception e) {
            throw new DBManagerException("Fail to drop " + e.getMessage(), e);
        }
    }

    @Override
    public int insert(List<Garden> gardens) throws DBManagerException {
        try {
            BulkWriteOperation builder = db.getCollection("Garden").initializeOrderedBulkOperation();
            for (Garden garden : gardens) {
                builder.insert(garden);
            }
            return builder.execute().getInsertedCount();
        } catch (Exception e) {
            throw new DBManagerException("Fail to bulk insert " + e.getMessage(), e);
        }
    }

}
