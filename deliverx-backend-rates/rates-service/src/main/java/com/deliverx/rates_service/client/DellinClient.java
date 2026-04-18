package com.deliverx.rates_service.client;

import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.carrier.DellinCalculatorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP-клиент к API Деловых Линий v2.
 *
 * Используем тип "auto" с вариантом address+address.
 * address.search принимает название города — ДЛ геокодирует сам.
 * Поле time обязательно для derival при варианте address.
 */
@Component
public class DellinClient {

    private static final Logger log = LoggerFactory.getLogger(DellinClient.class);

    private static final String BASE_URL  = "https://api.dellin.ru";
    private static final String CALC_PATH = "/v2/calculator.json";
    private static final int    MAX_BUF   = 10 * 1024 * 1024;

    @Value("${dellin.appkey}")
    private String appkey;

    private final WebClient webClient;

    public DellinClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl(BASE_URL)
                .codecs(c -> c.defaultCodecs().maxInMemorySize(MAX_BUF))
                .build();
    }

    public DellinCalculatorResponse calculate(RateRequest req) {
        double volume = (req.getWidthCm()  / 100.0)
                      * (req.getLengthCm() / 100.0)
                      * (req.getHeightCm() / 100.0);

        Map<String, Object> body = new HashMap<>();
        body.put("appkey", appkey);

        Map<String, Object> delivery = new HashMap<>();

        Map<String, Object> deliveryType = new HashMap<>();
        deliveryType.put("type", "auto");
        delivery.put("deliveryType", deliveryType);

        // Откуда — address с поиском по городу + обязательное время
        Map<String, Object> derival = new HashMap<>();
        derival.put("variant", "address");
        derival.put("produceDate", LocalDate.now().plusDays(1).toString());
        Map<String, Object> derivalAddr = new HashMap<>();
        derivalAddr.put("search", req.getFromCity());
        derival.put("address", derivalAddr);
        Map<String, Object> derivalTime = new HashMap<>();
        derivalTime.put("worktimeStart", "09:00");
        derivalTime.put("worktimeEnd", "18:00");
        derival.put("time", derivalTime);
        delivery.put("derival", derival);

        // Куда — address с поиском по городу
        Map<String, Object> arrival = new HashMap<>();
        arrival.put("variant", "address");
        Map<String, Object> arrivalAddr = new HashMap<>();
        arrivalAddr.put("search", req.getToCity());
        arrival.put("address", arrivalAddr);
        delivery.put("arrival", arrival);

        body.put("delivery", delivery);

        Map<String, Object> cargo = new HashMap<>();
        cargo.put("quantity",    1);
        cargo.put("length",      req.getLengthCm() / 100.0);
        cargo.put("width",       req.getWidthCm()  / 100.0);
        cargo.put("height",      req.getHeightCm() / 100.0);
        cargo.put("weight",      req.getWeightKg());
        cargo.put("totalVolume", volume);
        cargo.put("totalWeight", req.getWeightKg());
        body.put("cargo", cargo);

        log.info("Dellin request: from='{}' to='{}' weight={}kg",
                req.getFromCity(), req.getToCity(), req.getWeightKg());

        try {
            return webClient.post()
                    .uri(CALC_PATH)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(DellinCalculatorResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Dellin API error {} {} {}: {}",
                    e.getStatusCode(), e.getStatusText(),
                    e.getRequest() != null ? e.getRequest().getURI() : "",
                    e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Dellin API call failed: {}", e.getMessage());
            return null;
        }
    }
}
