package com.kaicao.garden.db;

import com.kaicao.garden.models.Garden;
import com.kaicao.garden.utils.MongoDBManagerException;
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
public class MongoDBManager {
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

    public void createGardenCollection() {
        // Cap 100 mega
        if (!getCollectionNames().contains("Garden")) {
            db.createCollection("Garden", (new BasicDBObject("capped", true).append("size", 104800000)));
        }
    }

    public Set<String> getCollectionNames() {
        return db.getCollectionNames();
    }

    public void insert(Garden garden) throws MongoDBManagerException {
        try {
            db.getCollection("Garden").save(garden);
        } catch (Exception e) {
            throw new MongoDBManagerException("Fail to insert garden " + e.getMessage(), e);
        }
    }

    public long count() {
        return db.getCollection("Garden").count();
    }

    public Garden findById(String id) throws MongoDBManagerException {
        try {
            BasicDBObject query = new BasicDBObject("id", id);
            DBCollection collection = db.getCollection("Garden");
            collection.setObjectClass(Garden.class);
            return (Garden) collection.find(query).next();
        } catch (Exception e) {
            throw new MongoDBManagerException("Fail to findById " + e.getMessage(), e);
        }
    }

    public List<Garden> queryValueRange(QueryRange<Integer> range) {
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
            throw new MongoDBManagerException("Fail to queryValueRange " + e.getMessage(), e);
        }
    }

    public void drop() {
        try {
            db.getCollection("Garden").drop();
        } catch (Exception e) {
            throw new MongoDBManagerException("Fail to drop " + e.getMessage(), e);
        }
    }

    public int insert(List<Garden> gardens) {
        try {
            BulkWriteOperation builder = db.getCollection("Garden").initializeOrderedBulkOperation();
            for (Garden garden : gardens) {
                builder.insert(garden);
            }
            return builder.execute().getInsertedCount();
        } catch (Exception e) {
            throw new MongoDBManagerException("Fail to bulk insert " + e.getMessage(), e);
        }
    }

}
