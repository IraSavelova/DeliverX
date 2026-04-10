package com.deliverx.rates_service.service;

import com.deliverx.rates_service.client.PekClient;
import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.RateResponse;
import com.deliverx.rates_service.dto.carrier.CarrierRateRequest;
import com.deliverx.rates_service.dto.carrier.CarrierRateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Оркестрирует запросы к перевозчикам и возвращает список тарифов.
 *
 * Сейчас интегрирован один перевозчик — ПЭК.
 * Когда добавятся другие (CDEK, Boxberry и т.д.) — каждый будет
 * отдельным клиентом, результаты сливаются в общий список.
 */
@Service
public class RatesService {

    private static final Logger log = LoggerFactory.getLogger(RatesService.class);

    private final PekClient pekClient;
    private final CityDictionaryService cityDictionary;

    public RatesService(PekClient pekClient, CityDictionaryService cityDictionary) {
        this.pekClient = pekClient;
        this.cityDictionary = cityDictionary;
    }

    /**
     * Рассчитывает тарифы всех доступных перевозчиков.
     *
     * @param request параметры груза и маршрута
     * @param sortBy  "price" | "time" | null (без сортировки)
     * @return список тарифов
     */
    public List<RateResponse> calculate(RateRequest request, String sortBy) {
        List<RateResponse> results = new ArrayList<>();

        // --- ПЭК ---
        List<RateResponse> pekRates = fetchPekRates(request);
        results.addAll(pekRates);

        // Сюда позже добавим: results.addAll(fetchCdekRates(request));

        // Сортировка
        if ("price".equalsIgnoreCase(sortBy)) {
            results.sort(Comparator.comparingDouble(RateResponse::getPrice));
        } else if ("time".equalsIgnoreCase(sortBy)) {
            results.sort(Comparator.comparingInt(RateResponse::getEstimatedDays));
        }

        return results;
    }

    /**
     * Запрашивает тарифы у ПЭК и маппит ответ в наш формат RateResponse.
     *
     * ПЭК возвращает отдельно авто и авиа — каждый тип становится отдельной строкой.
     */
    private List<RateResponse> fetchPekRates(RateRequest request) {
        List<RateResponse> rates = new ArrayList<>();

        // Конвертируем названия городов в PEK ID
        String fromId = cityDictionary.findCityId(request.getFromCity());
        String toId   = cityDictionary.findCityId(request.getToCity());

        if (fromId == null || toId == null) {
            log.warn("PEK: city not found. from='{}' (id={}) to='{}' (id={})",
                    request.getFromCity(), fromId, request.getToCity(), toId);
            return rates;
        }

        // Конвертируем см -> м
        double widthM  = request.getWidthCm()  / 100.0;
        double lengthM = request.getLengthCm() / 100.0;
        double heightM = request.getHeightCm() / 100.0;

        CarrierRateRequest pekRequest = new CarrierRateRequest(
                fromId, toId, widthM, lengthM, heightM, request.getWeightKg()
        );

        CarrierRateResponse pekResponse = pekClient.calculate(pekRequest);

        if (pekResponse == null) {
            log.warn("PEK: no response received");
            return rates;
        }

        // Автоперевозка
        if (pekResponse.hasAuto()) {
            rates.add(new RateResponse(
                    "ПЭК",
                    pekResponse.getTotalAutoPrice(),
                    pekResponse.getAutoDays(),
                    "COURIER"
            ));
        }

        // Авиаперевозка (если доступна на данном направлении)
        if (pekResponse.hasAvia()) {
            rates.add(new RateResponse(
                    "ПЭК Авиа",
                    pekResponse.getTotalAviaPrice(),
                    1, // авиа обычно 1 день, PEK пишет в aperiods строкой
                    "COURIER"
            ));
        }

        return rates;
    }
}
