package com.kaicao.garden.controllers;

import com.kaicao.garden.models.Garden;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kaicao on 26/10/14.
 */
public class GardenVOTest {

    @Test
    public void copyTest() throws Exception {
        Garden garden = Garden.createGarden("name", 1);
        GardenVO vo = new GardenVO(garden);
        assertEquals(garden.getId(), vo.getId());
        assertEquals(garden.getName(), vo.getName());
        assertEquals(garden.getDate(), vo.getDate());
        assertEquals(garden.getValue(), vo.getValue());
    }
}
