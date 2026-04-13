package com.deliverx.rates_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class CityDictionaryService {

    private static final Logger log = LoggerFactory.getLogger(CityDictionaryService.class);
    private static final String TOWNS_URL = "https://pecom.ru/ru/calc/towns.php";

    // 10MB — PEK возвращает большой справочник всех городов России
    private static final int MAX_BUFFER_SIZE = 10 * 1024 * 1024;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private Map<String, String> cityCache;

    public CityDictionaryService(WebClient.Builder builder, ObjectMapper objectMapper) {
        this.webClient = builder
                .codecs(c -> c.defaultCodecs().maxInMemorySize(MAX_BUFFER_SIZE))
                .build();
        this.objectMapper = objectMapper;
    }

    public String findCityId(String cityName) {
        if (cityCache == null) {
            loadCities();
        }
        return cityCache.get(cityName.toLowerCase().trim());
    }

    private void loadCities() {
        cityCache = new HashMap<>();
        try {
            String json = webClient.get()
                    .uri(TOWNS_URL)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (json == null) {
                log.warn("PEK towns API returned null");
                return;
            }

            Map<String, Map<String, String>> regions = objectMapper.readValue(
                    json, new TypeReference<>() {});

            for (Map<String, String> cities : regions.values()) {
                for (Map.Entry<String, String> entry : cities.entrySet()) {
                    cityCache.put(entry.getValue().toLowerCase().trim(), entry.getKey());
                }
            }

            log.info("Loaded {} cities from PEK dictionary", cityCache.size());

        } catch (Exception e) {
            log.error("Failed to fetch PEK towns: {}", e.getMessage());
        }
    }
}
