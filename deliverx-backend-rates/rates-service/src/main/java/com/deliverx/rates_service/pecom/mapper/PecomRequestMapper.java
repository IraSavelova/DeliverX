package com.deliverx.rates_service.pecom.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Component;

import com.deliverx.rates_service.dto.RateRequest;
import com.deliverx.rates_service.pecom.dto.PecomPlace;
import com.deliverx.rates_service.pecom.dto.PecomRequestParams;

@Component
public class PecomRequestMapper {

    public PecomRequestParams map(RateRequest request, Integer fromTownId, Integer toTownId) {
        PecomRequestParams params = new PecomRequestParams();
        params.setTakeTownId(fromTownId);
        params.setDeliverTownId(toTownId);

        BigDecimal lengthM = cmToM(request.getLengthCm());
        BigDecimal widthM = cmToM(request.getWidthCm());
        BigDecimal heightM = cmToM(request.getHeightCm());

        PecomPlace place = new PecomPlace();
        place.setLengthM(lengthM);
        place.setWidthM(widthM);
        place.setHeightM(heightM);
        place.setVolumeM3(lengthM.multiply(widthM).multiply(heightM).setScale(6, RoundingMode.HALF_UP));
        place.setWeightKg(BigDecimal.valueOf(request.getWeightKg()));
        place.setOversized(0);
        place.setPackageProtection(0);

        params.setPlaces(List.of(place));
        return params;
    }

    private BigDecimal cmToM(Integer cm) {
        return BigDecimal.valueOf(cm)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
    }
}