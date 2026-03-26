package com.deliverx.rates_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.RateResponse;

@RestController
@RequestMapping("/rates")
public class RatesController {

    @PostMapping("/calculate")
    public List<RateResponse> calculateRates(@RequestBody RateRequest request) {
        return List.of(
                new RateResponse("FastShip", 1290.0, 2, "COURIER"),
                new RateResponse("EcoDelivery", 890.0, 4, "PICKUP_POINT")
        );
    }
}