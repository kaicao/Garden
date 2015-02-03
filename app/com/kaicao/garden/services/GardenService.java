package com.kaicao.garden.services;

import com.kaicao.garden.db.DBManager;
import com.kaicao.garden.db.QueryRange;
import com.kaicao.garden.models.Garden;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by kaicao on 26/10/14.
 */
@Singleton
public class GardenService {

    @Inject
    DBManager dbManager;

    public void addGarden(Garden garden) {
        dbManager.insert(garden);
    }

    public Garden findGardenById(String id) {
        return dbManager.findById(id);
    }

    public List<Garden> getGardensByValueRange(Integer min, Integer max) {
        return dbManager.queryValueRange(new QueryRange<>(min, true, max, true));
    }

    public void dropGardens() {
        dbManager.close();
    }

    public int addGardens(List<Garden> gardens) {
        return dbManager.insert(gardens);
    }

}
