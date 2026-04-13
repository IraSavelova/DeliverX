package com.deliverx.rates_service.dto.carrier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Ответ от справочника городов Деловых Линий.
 * POST https://api.dellin.ru/v2/public/cities.json
 *
 * Возвращает список городов с их КЛАДР-кодами.
 * КЛАДР-код — 25-значный идентификатор населённого пункта в России.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DellinCityResponse {

    @JsonProperty("cities")
    private List<City> cities;

    public List<City> getCities() { return cities; }
    public void setCities(List<City> cities) { this.cities = cities; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {

        @JsonProperty("code")
        private String code; // КЛАДР-код, например "7700000000000000000000000"

        @JsonProperty("name")
        private String name; // "Москва"

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
