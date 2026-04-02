package com.deliverx.rates_service.pecom.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.deliverx.rates_service.pecom.dto.PecomCalcResponse;
import com.deliverx.rates_service.pecom.dto.PecomPlace;
import com.deliverx.rates_service.pecom.dto.PecomRequestParams;

@Component
public class PecomCalcClient {

    private static final String CALC_URL = "http://calc.pecom.ru/bitrix/components/pecom/calc/ajax.php";

    private final RestTemplate restTemplate;

    public PecomCalcClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PecomCalcResponse calculate(PecomRequestParams params) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        for (int i = 0; i < params.getPlaces().size(); i++) {
            PecomPlace place = params.getPlaces().get(i);

            queryParams.add("places[" + i + "][]", place.getWidthM().toPlainString());
            queryParams.add("places[" + i + "][]", place.getLengthM().toPlainString());
            queryParams.add("places[" + i + "][]", place.getHeightM().toPlainString());
            queryParams.add("places[" + i + "][]", place.getVolumeM3().toPlainString());
            queryParams.add("places[" + i + "][]", place.getWeightKg().toPlainString());
            queryParams.add("places[" + i + "][]", String.valueOf(place.getOversized()));
            queryParams.add("places[" + i + "][]", String.valueOf(place.getPackageProtection()));
        }

        queryParams.add("take[town]", String.valueOf(params.getTakeTownId()));
        queryParams.add("deliver[town]", String.valueOf(params.getDeliverTownId()));

        queryParams.add("take[tent]", "0");
        queryParams.add("take[gidro]", "0");
        queryParams.add("take[manip]", "0");
        queryParams.add("take[speed]", "0");
        queryParams.add("take[moscow]", "0");

        queryParams.add("deliver[tent]", "0");
        queryParams.add("deliver[gidro]", "0");
        queryParams.add("deliver[manip]", "0");
        queryParams.add("deliver[speed]", "0");
        queryParams.add("deliver[moscow]", "0");

        queryParams.add("plombir", "0");
        queryParams.add("strah", "0");
        queryParams.add("ashan", "0");
        queryParams.add("night", "0");
        queryParams.add("pal", "0");
        queryParams.add("pallets", "0");
        String url = UriComponentsBuilder
                .fromUriString(CALC_URL)
                .queryParams(queryParams)
                .build(false)
                .toUriString();

        ResponseEntity<PecomCalcResponse> response =
                restTemplate.getForEntity(url, PecomCalcResponse.class);

        return response.getBody();
    }
}