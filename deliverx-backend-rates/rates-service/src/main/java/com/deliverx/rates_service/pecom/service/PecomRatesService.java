package com.deliverx.rates_service.pecom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.RateResponse;
import com.deliverx.rates_service.pecom.client.PecomCalcClient;
import com.deliverx.rates_service.pecom.dto.PecomCalcResponse;
import com.deliverx.rates_service.pecom.dto.PecomRequestParams;
import com.deliverx.rates_service.pecom.mapper.PecomRequestMapper;
import com.deliverx.rates_service.pecom.mapper.PecomResponseMapper;
import com.deliverx.rates_service.pecom.resolver.PecomTownResolver;

@Service
public class PecomRatesService {

    private final PecomTownResolver pecomTownResolver;
    private final PecomRequestMapper pecomRequestMapper;
    private final PecomCalcClient pecomCalcClient;
    private final PecomResponseMapper pecomResponseMapper;

    public PecomRatesService(
            PecomTownResolver pecomTownResolver,
            PecomRequestMapper pecomRequestMapper,
            PecomCalcClient pecomCalcClient,
            PecomResponseMapper pecomResponseMapper
    ) {
        this.pecomTownResolver = pecomTownResolver;
        this.pecomRequestMapper = pecomRequestMapper;
        this.pecomCalcClient = pecomCalcClient;
        this.pecomResponseMapper = pecomResponseMapper;
    }

    public List<RateResponse> calculateRates(RateRequest request) {
        Integer fromTownId = pecomTownResolver.resolveTownId(request.getFromCity());
        Integer toTownId = pecomTownResolver.resolveTownId(request.getToCity());

        PecomRequestParams pecomRequest = pecomRequestMapper.map(request, fromTownId, toTownId);
        PecomCalcResponse pecomResponse = pecomCalcClient.calculate(pecomRequest);

        return pecomResponseMapper.map(pecomResponse, request);
    }
}