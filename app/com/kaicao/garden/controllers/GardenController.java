package com.kaicao.garden.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaicao.garden.models.Garden;
import com.kaicao.garden.services.GardenService;
import com.kaicao.garden.utils.Helpers;
import com.kaicao.garden.utils.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger LOG = LoggerFactory.getLogger(GardenController.class);
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
            LOG.error("Failed to add garden", e);
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
            LOG.error("Error when getGardenById", e);
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
            LOG.error("Fail to queryGardenValue", e);
            return internalServerError(e.getMessage());
        }
    }

    public static Result resetGardens() {
        try {
            gardenService.dropGardens();
            return ok();
        } catch (Exception e) {
            LOG.error("Fail to resetGardens", e);
            return internalServerError(e.getMessage());
        }
    }
}
