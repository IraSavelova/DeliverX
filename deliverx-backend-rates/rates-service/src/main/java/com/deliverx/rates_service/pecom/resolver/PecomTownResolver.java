package com.deliverx.rates_service.pecom.resolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class PecomTownResolver {

    private final Map<String, Integer> cityToId = new HashMap<>();

    public PecomTownResolver() {
        cityToId.put("екатеринбург", -457);
        cityToId.put("москва", 446);
        cityToId.put("санкт-петербург", 64883);
        cityToId.put("ростов-на-дону", 452);
        cityToId.put("алматы", -112966);
        cityToId.put("абакан", -584988);
    }

    public Integer resolveTownId(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            throw new IllegalArgumentException("Название города пустое");
        }

        Integer id = cityToId.get(normalize(cityName));
        if (id == null) {
            throw new IllegalArgumentException("Город не найден в локальном справочнике PECOM: " + cityName);
        }

        return id;
    }

    private String normalize(String value) {
        return value.trim()
                .toLowerCase(Locale.ROOT)
                .replace("ё", "е");
    }
}