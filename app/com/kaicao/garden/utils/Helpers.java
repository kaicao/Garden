package com.kaicao.garden.utils;

import com.fasterxml.jackson.databind.*;
import com.kaicao.garden.controllers.GardenVO;

/**
 * Created by kaicao on 25/10/14.
 */
public final class Helpers {
    public final static ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(MapperFeature.AUTO_DETECT_CREATORS, false)
                .configure(MapperFeature.AUTO_DETECT_FIELDS, false)
                .configure(MapperFeature.AUTO_DETECT_SETTERS, false)
                .configure(MapperFeature.AUTO_DETECT_GETTERS, false)
                .configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
    }
    public final static ObjectReader GARDEN_VO_READER = MAPPER.reader(GardenVO.class);
    public final static ObjectWriter GARDEN_VO_WRITER = MAPPER.writerWithType(GardenVO.class);
}
