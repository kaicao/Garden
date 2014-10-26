package com.kaicao.garden;

import com.google.inject.AbstractModule;
import com.kaicao.garden.controllers.GardenController;
import com.kaicao.garden.db.MongoDBManager;
import com.kaicao.garden.services.GardenService;

/**
 * Created by kaicao on 26/10/14.
 */
public class GardenModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MongoDBManager.class);
        bind(GardenService.class);

        requestStaticInjection(GardenController.class);
    }
}
