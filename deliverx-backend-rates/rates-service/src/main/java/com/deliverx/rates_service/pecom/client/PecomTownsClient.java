package com.deliverx.rates_service.pecom.client;

import java.util.Collections;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PecomTownsClient {

    private static final String TOWNS_URL = "https://pecom.ru/ru/calc/towns.php";

    private final RestTemplate restTemplate;

    public PecomTownsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Map<String, String>> loadTowns() {
        ResponseEntity<Map<String, Map<String, String>>> response = restTemplate.exchange(
                TOWNS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Map<String, String>>>() {}
        );

        Map<String, Map<String, String>> body = response.getBody();
        return body != null ? body : Collections.emptyMap();
    }
}