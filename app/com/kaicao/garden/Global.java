package com.kaicao.garden;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kaicao.garden.db.MongoDBManager;
import play.Application;
import play.GlobalSettings;
import play.Play;

/**
 * Created by kaicao on 26/10/14.
 */
public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        if (!Play.isTest()) {
            Injector injector = Guice.createInjector(new GardenModule());
            MongoDBManager mongoDBManager = injector.getInstance(MongoDBManager.class);
            mongoDBManager.createGardenCollection();
        }
    }
}
