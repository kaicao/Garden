package com.kaicao.garden;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kaicao.garden.db.DBManager;
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
            DBManager dbManager = injector.getInstance(DBManager.class);
            dbManager.init();
        }
    }
}
