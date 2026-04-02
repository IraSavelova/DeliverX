package com.deliverx.rates_service.pecom.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.dto.RateResponse;
import com.deliverx.rates_service.pecom.dto.PecomCalcResponse;
import com.deliverx.rates_service.pecom.dto.PecomDeliveryBlock;

@Component
public class PecomResponseMapper {

    public List<RateResponse> map(PecomCalcResponse response, RateRequest request) {
        if (response == null) {
            throw new IllegalStateException("PECOM не вернул ответ");
        }

        if (response.getError() != null && !response.getError().isEmpty()) {
            throw new IllegalStateException(String.join("; ", response.getError()));
        }

        List<RateResponse> result = new ArrayList<>();

        double takePrice = PecomDeliveryBlock.fromRawList(response.getTake()).getPrice();
        double deliverPrice = PecomDeliveryBlock.fromRawList(response.getDeliver()).getPrice();
        double addPrice = 0.0
                + PecomDeliveryBlock.fromRawList(response.getADD()).getPrice()
                + PecomDeliveryBlock.fromRawList(response.getADD_1()).getPrice()
                + PecomDeliveryBlock.fromRawList(response.getADD_2()).getPrice()
                + PecomDeliveryBlock.fromRawList(response.getADD_3()).getPrice()
                + PecomDeliveryBlock.fromRawList(response.getADD_4()).getPrice();

        if (response.getAuto() != null) {
            double linehaul = response.getAutonegabarit() != null
                    ? PecomDeliveryBlock.fromRawList(response.getAutonegabarit()).getPrice()
                    : PecomDeliveryBlock.fromRawList(response.getAuto()).getPrice();

            double alma = PecomDeliveryBlock.fromRawList(response.getAlma_auto()).getPrice();

            result.add(new RateResponse(
                    "PECOM",
                    takePrice + linehaul + alma + deliverPrice + addPrice,
                    estimateDays(request.getDeliverySpeed(), response.getPeriods()),
                    "COURIER"
            ));
        }

        if (response.getAvia() != null) {
            double avia = PecomDeliveryBlock.fromRawList(response.getAvia()).getPrice();

            result.add(new RateResponse(
                    "PECOM AVIA",
                    takePrice + avia + deliverPrice + addPrice,
                    estimateDays(request.getDeliverySpeed(), response.getAperiods()),
                    "COURIER"
            ));
        }

        return result;
    }

    private Integer estimateDays(String deliverySpeed, String periodsText) {
        if ("express".equalsIgnoreCase(deliverySpeed)) {
            return 1;
        }
        if ("economy".equalsIgnoreCase(deliverySpeed)) {
            return 5;
        }

        if (periodsText == null || periodsText.isBlank()) {
            return 3;
        }

        String digits = periodsText.replaceAll("[^0-9]", " ").trim();
        if (digits.isBlank()) {
            return 3;
        }

        String[] parts = digits.split("\\s+");
        try {
            return Integer.parseInt(parts[0]);
        } catch (Exception e) {
            return 3;
        }
    }
}