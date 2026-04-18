package com.deliverx.rates_service.service;

import com.deliverx.rates_service.client.DellinClient;
import com.deliverx.rates_service.client.PekClient;
import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.RateResponse;
import com.deliverx.rates_service.dto.carrier.CarrierRateRequest;
import com.deliverx.rates_service.dto.carrier.CarrierRateResponse;
import com.deliverx.rates_service.dto.carrier.DellinCalculatorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RatesService {

    private static final Logger log = LoggerFactory.getLogger(RatesService.class);

    private final PekClient pekClient;
    private final CityDictionaryService cityDictionary;
    private final DellinClient dellinClient;

    public RatesService(PekClient pekClient,
                        CityDictionaryService cityDictionary,
                        DellinClient dellinClient) {
        this.pekClient = pekClient;
        this.cityDictionary = cityDictionary;
        this.dellinClient = dellinClient;
    }

    /**
     * Ключ кэша = fromCity + toCity + weightKg + lengthCm + widthCm + heightCm.
     * Одинаковые запросы в течение 10 минут не идут к перевозчикам повторно.
     */
    @Cacheable(
        value = "rates",
        key = "#request.fromCity + '-' + #request.toCity + '-' + #request.weightKg" +
              "+ '-' + #request.lengthCm + '-' + #request.widthCm + '-' + #request.heightCm"
    )
    public List<RateResponse> calculate(RateRequest request, String sortBy) {
        log.info("Считаем тарифы: {} → {} (кэш промах)",
                request.getFromCity(), request.getToCity());

        List<RateResponse> results = new ArrayList<>();

        log.info("Запрашиваем ПЭК...");
        results.addAll(fetchPekRates(request));

        log.info("Запрашиваем Деловые Линии...");
        results.addAll(fetchDellinRates(request));

        if ("price".equalsIgnoreCase(sortBy)) {
            results.sort(Comparator.comparingDouble(RateResponse::getPrice));
        } else if ("time".equalsIgnoreCase(sortBy)) {
            results.sort(Comparator.comparingInt(RateResponse::getEstimatedDays));
        }

        return results;
    }

    private List<RateResponse> fetchPekRates(RateRequest request) {
        List<RateResponse> rates = new ArrayList<>();

        String fromId = cityDictionary.findCityId(request.getFromCity());
        String toId   = cityDictionary.findCityId(request.getToCity());

        if (fromId == null || toId == null) {
            log.warn("ПЭК: город не найден. from='{}' (id={}) to='{}' (id={})",
                    request.getFromCity(), fromId, request.getToCity(), toId);
            return rates;
        }

        CarrierRateRequest pekRequest = new CarrierRateRequest(
                fromId, toId,
                request.getWidthCm()  / 100.0,
                request.getLengthCm() / 100.0,
                request.getHeightCm() / 100.0,
                request.getWeightKg()
        );

        CarrierRateResponse pekResponse = pekClient.calculate(pekRequest);
        if (pekResponse == null) {
            log.warn("ПЭК: нет ответа");
            return rates;
        }

        if (pekResponse.hasAuto()) {
            rates.add(new RateResponse("ПЭК", pekResponse.getTotalAutoPrice(),
                    pekResponse.getAutoDays(), "PICKUP_POINT"));
        }
        if (pekResponse.hasAvia()) {
            rates.add(new RateResponse("ПЭК Авиа", pekResponse.getTotalAviaPrice(),
                    1, "COURIER"));
        }

        return rates;
    }

    private List<RateResponse> fetchDellinRates(RateRequest request) {
        List<RateResponse> rates = new ArrayList<>();

        DellinCalculatorResponse response = dellinClient.calculate(request);

        if (response == null || !response.hasData()) {
            log.warn("Деловые Линии: нет ответа");
            return rates;
        }

        DellinCalculatorResponse.Data data = response.getData();
        int days = data.getDays();

        double autoPrice = data.getAutoPrice();
        if (autoPrice > 0) {
            rates.add(new RateResponse("Деловые Линии", autoPrice, days, "PICKUP_POINT"));
        }

        double aviaPrice = data.getAviaPrice();
        if (aviaPrice > 0) {
            rates.add(new RateResponse("Деловые Линии Авиа", aviaPrice, 1, "COURIER"));
        }

        double expressPrice = data.getExpressPrice();
        if (expressPrice > 0) {
            rates.add(new RateResponse("Деловые Линии Экспресс", expressPrice,
                    Math.max(1, days - 1), "COURIER"));
        }

        log.info("Деловые Линии: авто={} авиа={} экспресс={} дней={}",
                autoPrice, aviaPrice, expressPrice, days);

        return rates;
    }
}
