package com.deliverx.rates_service.client;

import com.deliverx.rates_service.dto.carrier.CarrierRateRequest;
import com.deliverx.rates_service.dto.carrier.CarrierRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
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


class PekClientTest {

    private PekClient client;

    @BeforeEach
    void setUp() {
        client = new PekClient(WebClient.builder());
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
    @DisplayName("URL содержит все обязательные параметры: places[0][], take, deliver")
    void requestUrlStructure() {
        List<ClientRequest> captured = stubHttp(req -> Mono.just(ClientResponse
                .create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{}")
                .build()));

        client.calculate(new CarrierRateRequest("-484", "-461", 0.20, 0.30, 0.10, 2.5));

        assertThat(captured).hasSize(1);
        // WebClient может закодировать [ и ] в %5B и %5D — нормализуем перед проверкой.
        String url = captured.get(0).url().toString()
                .replace("%5B", "[")
                .replace("%5D", "]");
        // PEK ожидает каждое из 7 значений массива и оба take/deliver
        assertThat(url).contains("places[0][]=0.2");
        assertThat(url).contains("places[0][]=0.3");
        assertThat(url).contains("places[0][]=0.1");
        assertThat(url).contains("places[0][]=2.5");
        assertThat(url).contains("take[town]=-484");
        assertThat(url).contains("deliver[town]=-461");
    }

    @Test
    @DisplayName("успешный JSON-ответ десериализуется в CarrierRateResponse")
    void successResponse() {
        stubHttp(req -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {
                      "take":    ["Забор", "описание", "500"],
                      "auto":    ["Авто", "описание", "1290.00"],
                      "deliver": ["Доставка", "описание", "300"],
                      "periods": "3-5 дней"
                    }
                    """)
                .build()));

        CarrierRateResponse resp = client.calculate(
                new CarrierRateRequest("-484", "-461", 0.1, 0.1, 0.1, 1));

        assertThat(resp).isNotNull();
        assertThat(resp.hasAuto()).isTrue();
        assertThat(resp.getTotalAutoPrice()).isEqualTo(2090.0);
        assertThat(resp.getAutoDays()).isEqualTo(3);
    }

    @Test
    @DisplayName("ошибка от PEK API → null, без выброса")
    void networkError() {
        stubHttp(req -> Mono.error(new RuntimeException("Timeout")));

        CarrierRateResponse resp = client.calculate(
                new CarrierRateRequest("-484", "-461", 0.1, 0.1, 0.1, 1));

        assertThat(resp).isNull();
    }

    @Test
    @DisplayName("ответ с массивом ошибок возвращается клиенту (валидное поведение)")
    void responseWithErrors() {
        stubHttp(req -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {
                      "error": ["Город не обслуживается"]
                    }
                    """)
                .build()));

        CarrierRateResponse resp = client.calculate(
                new CarrierRateRequest("-1", "-2", 0.1, 0.1, 0.1, 1));

        assertThat(resp).isNotNull();
        assertThat(resp.getError()).containsExactly("Город не обслуживается");
        assertThat(resp.hasAuto()).isFalse();
    }
}
