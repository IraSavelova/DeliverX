package com.deliverx.rates_service.client;

import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.carrier.DellinCalculatorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DellinClientTest {

    private static final String APPKEY = "test-dellin-appkey";
    private DellinClient client;

    @BeforeEach
    void setUp() {
        client = new DellinClient(WebClient.builder());
        ReflectionTestUtils.setField(client, "appkey", APPKEY);
    }

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

    @Test
    @DisplayName("успешный 200 OK → DTO десериализуется")
    void success() {
        stubHttp(req -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {
                      "data": {
                        "price": 1957.0,
                        "deliveryTerm": 7,
                        "availableDeliveryTypes": {"auto": 800.0, "avia": 1500.0}
                      }
                    }
                    """)
                .build()));

        DellinCalculatorResponse resp = client.calculate(request());

        assertThat(resp).isNotNull();
        assertThat(resp.hasData()).isTrue();
        assertThat(resp.getData().getAutoPrice()).isEqualTo(1957.0);
        assertThat(resp.getData().getDeliveryTerm()).isEqualTo(7);
    }

    @Test
    @DisplayName("4xx → null без выброса исключения")
    void clientError() {
        stubHttp(req -> Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST)
                .body("{\"error\":\"bad request\"}")
                .build()));

        assertThat(client.calculate(request())).isNull();
    }

    @Test
    @DisplayName("сетевая ошибка → null")
    void networkError() {
        stubHttp(req -> Mono.error(new RuntimeException("Connection refused")));

        assertThat(client.calculate(request())).isNull();
    }

    @Test
    @DisplayName("rate limit 429 → null")
    void rateLimit() {
        stubHttp(req -> Mono.just(ClientResponse.create(HttpStatus.TOO_MANY_REQUESTS)
                .body("{\"error\":\"too many requests\"}")
                .build()));

        assertThat(client.calculate(request())).isNull();
    }

    @Test
    @DisplayName("запрос отправляется методом POST на /v2/calculator.json")
    void requestShape() {
        List<ClientRequest> captured = stubHttp(req -> Mono.just(
                ClientResponse.create(HttpStatus.OK).body("{}").build()));

        client.calculate(request());

        assertThat(captured).hasSize(1);
        ClientRequest req = captured.get(0);
        assertThat(req.method()).isEqualTo(HttpMethod.POST);
        assertThat(req.url().getPath()).isEqualTo("/v2/calculator.json");
    }

    @Test
    @DisplayName("в случае null-данных в body сервиса всё равно возвращается объект")
    void emptyDataField() {
        stubHttp(req -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{}")
                .build()));

        DellinCalculatorResponse resp = client.calculate(request());

        assertThat(resp).isNotNull();
        assertThat(resp.hasData()).isFalse();
    }

    private RateRequest request() {
        RateRequest r = new RateRequest();
        r.setFromCity("Москва");
        r.setToCity("Санкт-Петербург");
        r.setWeightKg(2.0);
        r.setLengthCm(40);
        r.setWidthCm(30);
        r.setHeightCm(20);
        return r;
    }
}
