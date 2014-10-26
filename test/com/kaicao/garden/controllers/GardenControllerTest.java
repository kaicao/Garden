package com.kaicao.garden.controllers;

import com.kaicao.garden.models.Garden;
import com.kaicao.garden.services.GardenService;
import com.kaicao.garden.utils.Helpers;
import org.junit.BeforeClass;
import org.junit.Test;
import play.mvc.Result;
import play.test.FakeRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static play.test.Helpers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Created by kaicao on 26/10/14.
 */
public class GardenControllerTest {

    private static GardenService gardenService;

    @BeforeClass
    public static void beforeClass() throws Exception {
        gardenService = mock(GardenService.class);
        GardenController.gardenService = gardenService;
    }

    @Test
    public void getGardenById() throws Exception {
        Garden garden = new Garden("test1", "name", new Date(), 1);
        when(gardenService.findGardenById(eq("test1"))).thenReturn(garden);

        Result result = routeAndCall(fakeRequest(GET, "/garden/test1"), 1000);
        assertEquals(200, status(result));
        assertEquals("application/json", contentType(result));

        String content = contentAsString(result);
        GardenVO vo = Helpers.GARDEN_VO_READER.readValue(content);
        assertEquals(garden.getId(), vo.getId());
        assertEquals(garden.getName(), vo.getName());
        assertEquals(garden.getDate(), vo.getDate());
        assertEquals(garden.getValue(), vo.getValue());
    }

    @Test
    public void queryGardenValue() throws Exception {
        List<Garden> gardens = new ArrayList<>();
        for (int i = 0; i < 20; i ++) {
            gardens.add(Garden.createGarden("Test" + i, i));
        }
        when(gardenService.getGardensByValueRange(eq(0), eq(20))).thenReturn(gardens);

        FakeRequest request = fakeRequest(GET, "/garden/query?min=0&max=20");
        Result result = routeAndCall(request, 1000);
        assertEquals(200, status(result));
        assertEquals("application/json", contentType(result));
        GardenController.QueryGardensResponse response = Helpers.MAPPER.reader(GardenController.QueryGardensResponse.class).readValue(contentAsBytes(result));
        assertEquals(20, response.gardens.size());
        assertEquals("Test0", response.gardens.get(0).getName());
        assertEquals(0, response.gardens.get(0).getValue());
        assertEquals("Test19", response.gardens.get(19).getName());
        assertEquals(19, response.gardens.get(19).getValue());
    }
}
