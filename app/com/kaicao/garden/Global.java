package com.kaicao.garden;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kaicao.fx.FXModule;
import com.kaicao.fx.repository.FXTickDataRepository;
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
        Injector injector = Guice.createInjector(new FXModule());
        /* MongoLab account expired, need get new one
        if (!Play.isTest()) {
            injector = Guice.createInjector(new GardenModule());
            DBManager dbManager = injector.getInstance(DBManager.class);
            dbManager.init();
        }*/
        injector.getInstance(FXTickDataRepository.class).init(null);
    }
}
