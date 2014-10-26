package com.kaicao.garden.services;

import com.kaicao.garden.db.MongoDBManager;
import com.kaicao.garden.db.QueryRange;
import com.kaicao.garden.models.Garden;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Set;

/**
 * Created by kaicao on 26/10/14.
 */
@Singleton
public class GardenService {

    @Inject
    MongoDBManager  mongoDBManager;

    public Set<String> getCollectionNames() {
        return mongoDBManager.getCollectionNames();
    }

    public void addGarden(Garden garden) {
        mongoDBManager.insert(garden);
    }

    public Garden findGardenById(String id) {
        return mongoDBManager.findById(id);
    }

    public List<Garden> getGardensByValueRange(Integer min, Integer max) {
        return mongoDBManager.queryValueRange(new QueryRange<>(min, true, max, true));
    }

    public void dropGardens() {
        mongoDBManager.drop();
    }

    public int addGardens(List<Garden> gardens) {
        return mongoDBManager.insert(gardens);
    }

}
