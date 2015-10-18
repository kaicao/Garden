package com.kaicao.passivedns;

import com.google.inject.AbstractModule;

/**
 * Created by kaicao on 18/10/15.
 */
public class PassiveDNSModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PassiveDNSRepository.class).to(PassiveDNSRepositoryImpl.class);
    }
}
