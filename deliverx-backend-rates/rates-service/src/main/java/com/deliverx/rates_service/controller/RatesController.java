package com.deliverx.rates_service.controller;

import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.RateResponse;
import com.deliverx.rates_service.service.RatesService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rates")
public class RatesController {

    private final RatesService ratesService;

    public RatesController(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    /**
     * POST /rates/calculate
     * POST /rates/calculate?sortBy=price
     * POST /rates/calculate?sortBy=time
     */
    @PostMapping("/calculate")
    public List<RateResponse> calculateRates(
            @Valid @RequestBody RateRequest request,
            @RequestParam(required = false) String sortBy) {
        return ratesService.calculate(request, sortBy);
    }
}
