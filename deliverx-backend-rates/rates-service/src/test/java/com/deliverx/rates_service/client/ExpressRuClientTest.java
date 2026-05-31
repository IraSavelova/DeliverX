package com.deliverx.rates_service.client;

import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.carrier.ExpressRuCalculatorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.reactive.MockClientHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressRuClientTest {

    private static final String CLIENT_ID = "TestClient1";
    private static final String AUTH_KEY  = "auth-key-base64";
    private static final String SIGN_KEY  = "test-sign-key";

    private ExpressRuClient client;

    @BeforeEach
    void setUp() {
        // Создаём настоящий клиент через настоящий Builder.
        client = new ExpressRuClient(WebClient.builder());
        ReflectionTestUtils.setField(client, "clientId",    CLIENT_ID);
        ReflectionTestUtils.setField(client, "authKey",     AUTH_KEY);
        ReflectionTestUtils.setField(client, "signKey",     SIGN_KEY);
        ReflectionTestUtils.setField(client, "signUrlMode", "full");
    }

    /** Заменяет внутренний webClient на тот, что вместо HTTP вызывает данную функцию. */
    private List<ClientRequest> stubHttp(ExchangeFunction fn) {
        List<ClientRequest> captured = new ArrayList<>();
        ExchangeFunction wrapped = req -> {
            captured.add(req);
            return fn.exchange(req);
        };
        WebClient stub = WebClient.builder().exchangeFunction(wrapped).build();
        ReflectionTestUtils.setField(client, "webClient", stub);
        return captured;
    }

    /** Готовый OK-ответ. */
    private static ClientResponse okJson(String body) {
        return ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build();
    }

    // ============================ ПОДПИСЬ ===============================

    @Nested
    @DisplayName("Алгоритм подписи")
    class Signing {

        @Test
        @DisplayName("совпадает с эталонной формулой md5(clientId+url+sortedParams+signKey)")
        void matchesReferenceFormula() {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("b", "2");
            params.put("a", "1");

            String expectedInput = CLIENT_ID + "/url" + "a=1b=2" + "" + SIGN_KEY;
            String expected = md5(expectedInput);

            String actual = client.sign("/url", params, Map.of());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("ключи сортируются лексикографически, независимо от порядка вставки")
        void keysSorted() {
            Map<String, String> unsorted = new LinkedHashMap<>();
            unsorted.put("z", "26");
            unsorted.put("a", "1");
            unsorted.put("m", "13");

            Map<String, String> sorted = new LinkedHashMap<>();
            sorted.put("a", "1");
            sorted.put("m", "13");
            sorted.put("z", "26");

            assertThat(client.sign("/x", unsorted, Map.of()))
                    .isEqualTo(client.sign("/x", sorted, Map.of()));
        }

        @Test
        @DisplayName("пустые GET и POST → md5(clientId+url+signKey)")
        void emptyParams() {
            String expected = md5(CLIENT_ID + "/url" + SIGN_KEY);
            assertThat(client.sign("/url", Map.of(), Map.of())).isEqualTo(expected);
        }

        @Test
        @DisplayName("кириллица в значениях кодируется UTF-8")
        void cyrillicUtf8() {
            Map<String, String> params = Map.of("city", "Москва");

            String result   = client.sign("/url", params, Map.of());
            String expected = md5(CLIENT_ID + "/url" + "city=Москва" + SIGN_KEY);

            assertThat(result).isEqualTo(expected);
            assertThat(result).hasSize(32);
        }

        @Test
        @DisplayName("изменение одного бита значения → совершенно другая подпись (avalanche MD5)")
        void avalanche() {
            String s1 = client.sign("/u", Map.of("w", "1"), Map.of());
            String s2 = client.sign("/u", Map.of("w", "2"), Map.of());
            assertThat(s1).isNotEqualTo(s2);
        }

        @Test
        @DisplayName("разные url → разные подписи")
        void urlMatters() {
            String s1 = client.sign("/one", Map.of("a", "1"), Map.of());
            String s2 = client.sign("/two", Map.of("a", "1"), Map.of());
            assertThat(s1).isNotEqualTo(s2);
        }

        @Test
        @DisplayName("POST-параметры тоже учитываются")
        void postParamsCounted() {
            String s1 = client.sign("/u", Map.of("a", "1"), Map.of());
            String s2 = client.sign("/u", Map.of("a", "1"), Map.of("data", "x"));
            assertThat(s1).isNotEqualTo(s2);
        }
    }

    // ====================== КОНФИГУРАЦИОННЫЕ ПРОВЕРКИ ====================

    @Nested
    @DisplayName("Конфигурация")
    class Configuration {

        @Test @DisplayName("пустой clientId → null без HTTP")
        void emptyClientId() {
            ReflectionTestUtils.setField(client, "clientId", "");
            assertThat(client.calculate(request())).isNull();
        }

        @Test @DisplayName("пустой authKey → null без HTTP")
        void emptyAuthKey() {
            ReflectionTestUtils.setField(client, "authKey", "");
            assertThat(client.calculate(request())).isNull();
        }

        @Test @DisplayName("пустой signKey → null без HTTP")
        void emptySignKey() {
            ReflectionTestUtils.setField(client, "signKey", "");
            assertThat(client.calculate(request())).isNull();
        }
    }

    // ============================ HTTP ===================================

    @Nested
    @DisplayName("HTTP-вызов")
    class HttpCall {

        @Test
        @DisplayName("успешный 200 OK → десериализация в DTO")
        void success() {
            stubHttp(req -> Mono.just(okJson("""
                {
                  "status": "ok",
                  "error": false,
                  "result": [
                    {
                      "name": "Базовый",
                      "typeLabel": "Стандартная",
                      "rawPrice": 525,
                      "dayFrom": 1,
                      "dayTo": 1
                    }
                  ]
                }
                """)));

            ExpressRuCalculatorResponse resp = client.calculate(request());

            assertThat(resp).isNotNull();
            assertThat(resp.isOk()).isTrue();
            assertThat(resp.getResult()).hasSize(1);
            assertThat(resp.getResult().get(0).getTypeLabel()).isEqualTo("Стандартная");
            assertThat(resp.getResult().get(0).getRawPrice()).isEqualTo(525.0);
        }

        @Test
        @DisplayName("403 Forbidden → null без выброса исключения")
        void forbidden() {
            stubHttp(req -> Mono.just(ClientResponse.create(HttpStatus.FORBIDDEN)
                    .body("{\"status\":\"error\",\"error\":true,\"error_msg\":\"Подпись неверна\"}")
                    .build()));

            assertThat(client.calculate(request())).isNull();
        }

        @Test
        @DisplayName("сетевая ошибка → null без выброса")
        void networkError() {
            stubHttp(req -> Mono.error(new RuntimeException("Connection refused")));

            assertThat(client.calculate(request())).isNull();
        }

        @Test
        @DisplayName("в запросе передаются ожидаемые параметры и Authorization-заголовок")
        void requestShape() {
            List<ClientRequest> captured = stubHttp(req ->
                    Mono.just(okJson("{\"status\":\"ok\",\"error\":false,\"result\":[]}")));

            client.calculate(request());

            assertThat(captured).hasSize(1);
            ClientRequest req = captured.get(0);
            URI uri = req.url();
            String query = uri.getRawQuery();

            // Проверяем путь (хост в тестах через ExchangeFunction может отсутствовать)
            assertThat(uri.getPath()).isEqualTo("/api/office/calculate");
            assertThat(query).contains("cargo_type=1");
            assertThat(query).contains("weight=1");        // целочисленный вес — без ".0"
            assertThat(query).contains("box_length=30");
            assertThat(query).contains("sig=");           // подпись передаётся

            assertThat(req.headers().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo(AUTH_KEY);
        }

        @Test
        @DisplayName("дробный вес 1.8 идёт в URL как '1.8'")
        void fractionalWeight() {
            List<ClientRequest> captured = stubHttp(req ->
                    Mono.just(okJson("{\"status\":\"ok\",\"error\":false,\"result\":[]}")));

            RateRequest r = request();
            r.setWeightKg(1.8);
            client.calculate(r);

            assertThat(captured.get(0).url().getRawQuery()).contains("weight=1.8");
        }
    }

    // ============================ Helpers ===============================

    private RateRequest request() {
        RateRequest r = new RateRequest();
        r.setFromCity("Москва");
        r.setToCity("Санкт-Петербург");
        r.setWeightKg(1.0);
        r.setLengthCm(30);
        r.setWidthCm(20);
        r.setHeightCm(10);
        return r;
    }

    private static String md5(String s) {
        try {
            byte[] bytes = MessageDigest.getInstance("MD5")
                    .digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
