package com.deliverx.rates_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.RateResponse;
import com.deliverx.rates_service.pecom.service.PecomRatesService;

@Service
public class RatesService {

    private final PecomRatesService pecomRatesService;

    public RatesService(PecomRatesService pecomRatesService) {
        this.pecomRatesService = pecomRatesService;
    }

    public List<RateResponse> calculateRates(RateRequest request) {
        return pecomRatesService.calculateRates(request);
    }
}