package com.kaicao.garden.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaicao.garden.models.Garden;
import com.kaicao.garden.services.GardenService;
import com.kaicao.garden.utils.Helpers;
import com.kaicao.garden.utils.ServiceException;
import org.apache.commons.lang3.StringUtils;
import play.mvc.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaicao on 25/10/14.
 */
public class GardenController extends Controller {

    public static class QueryGardensResponse {
        @JsonProperty
        public List<GardenVO> gardens;

        @JsonCreator
        public QueryGardensResponse(@JsonProperty("gardens") List<GardenVO> gardens) {
            this.gardens = gardens;
        }
    }

    @Inject
    static GardenService gardenService;

    public static Result index() {
        return ok("Hello");
    }

    public static Result addGarden(String name, Integer value) {
        try {
            if (StringUtils.isBlank(name) || value == null) {
                return badRequest();
            }
            gardenService.addGarden(Garden.createGarden(name, value));
            return created();
        } catch (ServiceException e) {
            return internalServerError(e.getMessage());
        }
    }

    public static Result getGardenById(String id) {
        try {
            if (StringUtils.isBlank(id)) {
                return badRequest();
            }
            Garden garden = gardenService.findGardenById(id);
            if (null == garden) {
                return notFound();
            }
            response().setContentType("application/json");
            return ok(Helpers.GARDEN_VO_WRITER.writeValueAsString(new GardenVO(garden)));
        } catch (Exception e) {
            System.err.println("Error when getGardenById: " + e.getMessage());
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public static Result queryGardenValue(Integer min, Integer max) {
        try {
            if (null == min && null == max) {
                return badRequest();
            }
            List<GardenVO> gardens = new ArrayList<>();
            for (Garden garden : gardenService.getGardensByValueRange(min, max)) {
                gardens.add(new GardenVO(garden));
            }
            QueryGardensResponse response = new QueryGardensResponse(gardens);
            response().setContentType("application/json");
            return ok(Helpers.MAPPER.writeValueAsString(response));
        } catch (Exception e) {
            System.err.println("Fail to queryGardenValue " + e.getMessage());
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public static Result resetGardens() {
        try {
            gardenService.dropGardens();
            return ok();
        } catch (Exception e) {
            System.err.println("Fail to resetGardens " + e.getMessage());
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }
}
