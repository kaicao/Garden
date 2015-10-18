package com.kaicao.fx.service;

import com.kaicao.fx.entity.FxTickRecordEntity;
import com.kaicao.fx.repository.FXTickDataRepository;
import com.kaicao.garden.utils.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by kaicao on 30/09/15.
 */
@Singleton
public class FXTickDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FXTickDataService.class);

    @Inject
    private FXTickDataRepository repository;

    public void saveFXTickRecord(String currencyPair, String dateTimeStr, double bid, double ask)
            throws ServiceException {
        try {
            repository.saveFxTickRecord(new FxTickRecordEntity(currencyPair, dateTimeStr, bid, ask));
        } catch (Exception e) {
            LOGGER.error("Fail to save FxTick record", e);
            throw new ServiceException();
        }
    }

}
