package com.kaicao.fx;

import com.google.inject.AbstractModule;
import com.kaicao.fx.repository.FXTickDataRepository;
import com.kaicao.fx.repository.impl.FXTickDataRepositoryImpl;
import com.kaicao.fx.service.FXTickDataService;

/**
 * Created by kaicao on 30/09/15.
 */
public class FXModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FXTickDataRepository.class).to(FXTickDataRepositoryImpl.class);
        bind(FXTickDataService.class);
    }
}
