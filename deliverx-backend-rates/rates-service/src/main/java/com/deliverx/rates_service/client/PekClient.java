package com.deliverx.rates_service.client;

import com.deliverx.rates_service.dto.carrier.CarrierRateRequest;
import com.deliverx.rates_service.dto.carrier.CarrierRateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * HTTP-клиент к публичному API ПЭК.
 *
 * PEK не требует авторизации — просто GET с параметрами.
 * Документация: https://pecom.ru (раздел "Публичный API")
 *
 * Параметры груза передаются как массивы:
 *   places[0][]=ширина&places[0][]=длина&places[0][]=высота&places[0][]=объём&places[0][]=вес&places[0][]=0&places[0][]=0
 *
 * Размеры — в метрах, объём считаем сами (ш*д*в).
 */
@Component
public class PekClient {

    private static final Logger log = LoggerFactory.getLogger(PekClient.class);

    private static final String CALC_URL =
            "http://calc.pecom.ru/bitrix/components/pecom/calc/ajax.php";

    private final WebClient webClient;

    public PekClient(WebClient.Builder builder) {
        this.webClient = builder
                .codecs(c -> c.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    /**
     * Запрашивает тарифы у PEK для заданных параметров груза.
     *
     * @param req параметры с ID городов и размерами в метрах/кг
     * @return ответ от PEK или null если запрос не удался
     */
    public CarrierRateResponse calculate(CarrierRateRequest req) {
        double volume = req.getWidthM() * req.getLengthM() * req.getHeightM();

        // Строим URI вручную — PEK использует PHP-стиль массивов places[0][]
        // UriComponentsBuilder не умеет дублировать одинаковые ключи нужным образом
        String url = CALC_URL
                + "?places[0][]=" + req.getWidthM()
                + "&places[0][]=" + req.getLengthM()
                + "&places[0][]=" + req.getHeightM()
                + "&places[0][]=" + String.format("%.4f", volume)
                + "&places[0][]=" + req.getWeightKg()
                + "&places[0][]=0"   // не негабарит
                + "&places[0][]=0"   // без ЗТУ
                + "&take[town]=" + req.getTakeTown()
                + "&take[gidro]=0"
                + "&take[tent]=0"
                + "&take[manip]=0"
                + "&deliver[town]=" + req.getDeliverTown()
                + "&deliver[gidro]=0"
                + "&deliver[tent]=0"
                + "&deliver[manip]=0";

        log.info("PEK request: {}", url);

        try {
            CarrierRateResponse response = webClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .bodyToMono(CarrierRateResponse.class)
                    .block();

            if (response != null && response.getError() != null && !response.getError().isEmpty()) {
                log.warn("PEK returned errors: {}", response.getError());
            }

            return response;
        } catch (Exception e) {
            log.error("PEK API call failed: {}", e.getMessage());
            return null;
        }
    }
}
