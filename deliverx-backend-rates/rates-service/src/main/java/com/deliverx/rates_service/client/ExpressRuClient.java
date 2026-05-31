package com.deliverx.rates_service.client;

import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.carrier.ExpressRuCalculatorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
public class ExpressRuClient {

    private static final Logger log = LoggerFactory.getLogger(ExpressRuClient.class);

    private static final String BASE_URL  = "https://www.express.ru";
    private static final String CALC_PATH = "/api/office/calculate";
    private static final int    MAX_BUF   = 1024 * 1024;

    /** Cargo type: 1 — груз, 2 — документы. */
    private static final int CARGO_TYPE_GENERAL = 1;

    @Value("${express.client-id:}")
    private String clientId;

    @Value("${express.auth-key:}")
    private String authKey;

    @Value("${express.sign-key:}")
    private String signKey;

    @Value("${express.sign-url-mode:full}")
    private String signUrlMode;

    private final WebClient webClient;

    public ExpressRuClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl(BASE_URL)
                .codecs(c -> c.defaultCodecs().maxInMemorySize(MAX_BUF))
                .build();
    }

    public ExpressRuCalculatorResponse calculate(RateRequest req) {
        if (clientId.isBlank() || authKey.isBlank() || signKey.isBlank()) {
            log.warn("Express.ru: не сконфигурированы ключи (express.client-id / "
                    + "express.auth-key / express.sign-key), пропускаем перевозчика");
            return null;
        }
        Map<String, String> params = new LinkedHashMap<>();
        params.put("country_from", "РОССИЯ");
        params.put("place_from",   req.getFromCity());
        params.put("country_to",   "РОССИЯ");
        params.put("place_to",     req.getToCity());
        params.put("cargo_type",   String.valueOf(CARGO_TYPE_GENERAL));
        params.put("weight",       formatWeight(req.getWeightKg()));
        params.put("box_length",   String.valueOf(req.getLengthCm()));
        params.put("box_width",    String.valueOf(req.getWidthCm()));
        params.put("box_height",   String.valueOf(req.getHeightCm()));
        params.put("items",        "1");
        params.put("fragile",      "0");

        String urlForSign = "path".equalsIgnoreCase(signUrlMode)
                ? CALC_PATH
                : BASE_URL + CALC_PATH;

        String signature = sign(urlForSign, params, Map.of());

        MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
        params.forEach(query::add);
        query.add("sig", signature);

        log.info("Express.ru request: {} → {}, weight={}kg, box={}x{}x{} cm",
                req.getFromCity(), req.getToCity(),
                req.getWeightKg(), req.getLengthCm(), req.getWidthCm(), req.getHeightCm());

        try {
            return webClient.get()
                    .uri(uri -> uri.path(CALC_PATH).queryParams(query).build())
                    .header(HttpHeaders.AUTHORIZATION, authKey)
                    .retrieve()
                    .bodyToMono(ExpressRuCalculatorResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Express.ru API error {} {}: {}",
                    e.getStatusCode(), e.getStatusText(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Express.ru API call failed: {}", e.getMessage());
            return null;
        }
    }

    String sign(String url, Map<String, String> getParams, Map<String, String> postParams) {
        String getConcat  = concat(getParams);
        String postConcat = concat(postParams);
        String toSign = clientId + url + getConcat + postConcat + signKey;
        String signature = md5(toSign);

        // Отладочный лог: показывает clientId/url/params, но скрывает signKey.
        if (log.isDebugEnabled()) {
            String redacted = clientId + url + getConcat + postConcat
                    + "<signKey:" + signKey.length() + "chars>";
            log.debug("Express.ru sign input ({} UTF-8 bytes): {}",
                    redacted.getBytes(StandardCharsets.UTF_8).length, redacted);
            log.debug("Express.ru sign output: {}", signature);
        }
        return signature;
    }

    private static String concat(Map<String, String> params) {
        if (params == null || params.isEmpty()) return "";
        TreeMap<String, String> sorted = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : sorted.entrySet()) {
            sb.append(e.getKey()).append('=').append(e.getValue());
        }
        return sb.toString();
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 not available", e);
        }
    }

    private static String formatWeight(double weight) {
        if (weight == Math.floor(weight) && !Double.isInfinite(weight)) {
            return String.valueOf((long) weight);
        }
        return String.valueOf(weight);
    }
}