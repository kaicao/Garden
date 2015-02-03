package com.kaicao.garden.db;

import com.kaicao.garden.models.Garden;
import com.kaicao.garden.utils.DBManagerException;
import com.mongodb.*;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by kaicao on 26/10/14.
 */
@Singleton
public class MongoDBManager implements DBManager {
    private class MongoDBProperties {
        private final static String PROPERTY_HOST = "mongodb.host";
        private final static String PROPERTY_PORT = "mongodb.port";
        private final static String PROPERTY_USERNAME = "mongodb.username";
        private final static String PROPERTY_PASSWORD = "mongodb.password";
        private final static String PROPERTY_DATABASE = "mongodb.database";
        private final Properties properties;

        private MongoDBProperties() throws IOException {
            properties = new Properties();
            try (InputStream inputStream = MongoDBManager.class.getClassLoader().getResourceAsStream("mongodb.properties")) {
                properties.load(inputStream);
            }
        }

        private String getHost() {
            return properties.getProperty(PROPERTY_HOST);
        }

        private int getPort() {
            return Integer.parseInt(properties.getProperty(PROPERTY_PORT));
        }

        private String getUsername() {
            return properties.getProperty(PROPERTY_USERNAME);
        }

        private String getPassword() {
            return properties.getProperty(PROPERTY_PASSWORD);
        }

        private String getDatabase() {
            return properties.getProperty(PROPERTY_DATABASE);
        }
    }

    private MongoClient client;
    private DB db;

    public MongoDBManager() throws Exception {
        try {
            MongoDBProperties properties = new MongoDBProperties();
            if (null == client && null == db) {
                client = new MongoClient(
                        new ServerAddress(properties.getHost(), properties.getPort()),
                        Arrays.asList(MongoCredential.createMongoCRCredential(properties.getUsername(), properties.getDatabase(), properties.getPassword().toCharArray()))
                );
                db = client.getDB(properties.getDatabase());
            }
        } catch (Exception e) {
            System.err.println("Not able to init MongoClient " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void init() throws DBManagerException {
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
