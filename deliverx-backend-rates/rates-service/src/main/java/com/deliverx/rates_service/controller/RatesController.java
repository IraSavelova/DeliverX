package com.deliverx.rates_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.RateResponse;
import com.deliverx.rates_service.service.RatesService;

@RestController
@RequestMapping("/rates")
public class RatesController {

    private final RatesService ratesService;

    public RatesController(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    @PostMapping("/calculate")
    public List<RateResponse> calculateRates(@RequestBody RateRequest request) {
        return ratesService.calculateRates(request);
    }
}