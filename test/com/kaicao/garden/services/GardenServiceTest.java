package com.kaicao.garden.services;

import com.kaicao.garden.db.MongoDBManager;
import com.kaicao.garden.models.Garden;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by kaicao on 26/10/14.
 */
public class GardenServiceTest {
    private static GardenService gardenService;

    @BeforeClass
    public static void beforeClass() throws Exception {
        gardenService = new GardenService();
        MongoDBManager mongoDBManager = new MongoDBManager();
        mongoDBManager.createGardenCollection();
        gardenService.mongoDBManager = mongoDBManager;
    }


    @After
    public void after() throws Exception {
        gardenService.dropGardens();
    }

    @Test
    public void addFindGarden() throws Exception {
        Garden garden = Garden.createGarden("Test1", 1);
        gardenService.addGarden(garden);
        Garden resultGarden = gardenService.findGardenById(garden.getId());
        assertEquals(garden.getId(), resultGarden.getId());
        assertEquals("Test1", resultGarden.getName());
        assertEquals(1, resultGarden.getValue());
    }

    @Test
    public void queryGardensByValueRange() throws Exception {
        List<Garden> gardens = new ArrayList<>();
        for (int i = 0; i < 20; i ++) {
            gardens.add(Garden.createGarden("Test" + i, i));
        }
        int insertCount = gardenService.addGardens(gardens);
        assertEquals(insertCount, gardenService.mongoDBManager.count());

        List<Garden> retGardens1 = gardenService.getGardensByValueRange(10, 30);
        assertEquals(10, retGardens1.size());
        assertEquals(10, retGardens1.get(0).getValue());
        assertEquals(19, retGardens1.get(9).getValue());

        List<Garden> retGardens2 = gardenService.getGardensByValueRange(null, 20);
        assertEquals(20, retGardens2.size());
        assertEquals(0, retGardens2.get(0).getValue());
        assertEquals(19, retGardens2.get(19).getValue());

        List<Garden> retGardens3 = gardenService.getGardensByValueRange(10, null);
        assertEquals(10, retGardens3.size());
        assertEquals(10, retGardens3.get(0).getValue());
        assertEquals(19, retGardens3.get(9).getValue());
    }

}
