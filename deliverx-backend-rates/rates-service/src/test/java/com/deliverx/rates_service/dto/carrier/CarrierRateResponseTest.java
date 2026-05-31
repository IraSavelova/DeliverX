package com.deliverx.rates_service.dto.carrier;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarrierRateResponseTest {

    @Test
    void hasAutoReturnsTrueWhenAutoArrayIsSufficient() {
        CarrierRateResponse r = new CarrierRateResponse();
        r.setAuto(List.of("name", "desc", "100"));

        assertThat(r.hasAuto()).isTrue();
    }

    @Test
    void hasAutoReturnsFalseWhenAutoIsNull() {
        assertThat(new CarrierRateResponse().hasAuto()).isFalse();
    }

    @Test
    void hasAutoReturnsFalseWhenAutoArrayIsTooShort() {
        CarrierRateResponse r = new CarrierRateResponse();
        r.setAuto(List.of("name", "desc"));   // нет цены

        assertThat(r.hasAuto()).isFalse();
    }

    @Test
    void hasAviaReturnsTrueWhenAviaArrayIsSufficient() {
        CarrierRateResponse r = new CarrierRateResponse();
        r.setAvia(List.of("Авиа", "опис", "2000"));

        assertThat(r.hasAvia()).isTrue();
    }

    @Test
    void hasAviaReturnsFalseWhenAviaIsNull() {
        assertThat(new CarrierRateResponse().hasAvia()).isFalse();
    }

    @Test
    void totalAutoPriceSumsTakeAutoDeliver() {
        CarrierRateResponse r = new CarrierRateResponse();
        r.setTake(List.of("", "", "100"));
        r.setAuto(List.of("", "", "500"));
        r.setDeliver(List.of("", "", "50"));

        assertThat(r.getTotalAutoPrice()).isEqualTo(650.0);
    }

    @Test
    void totalAutoPriceWithMissingComponentsCountsZero() {
        CarrierRateResponse r = new CarrierRateResponse();
        r.setAuto(List.of("", "", "500"));
        // take и deliver — null

        assertThat(r.getTotalAutoPrice()).isEqualTo(500.0);
    }

    @Test
    void totalAutoPriceParsesPriceWithSpaces() {
        // PEK иногда возвращает "1 290.00" — должны корректно распарсить
        CarrierRateResponse r = new CarrierRateResponse();
        r.setTake(List.of("", "", "100.00"));
        r.setAuto(List.of("", "", "1 290.00"));
        r.setDeliver(List.of("", "", "50.00"));

        assertThat(r.getTotalAutoPrice()).isEqualTo(1440.0);
    }

    @Test
    void totalAutoPriceIgnoresInvalidNumeric() {
        CarrierRateResponse r = new CarrierRateResponse();
        r.setAuto(List.of("", "", "not-a-number"));

        assertThat(r.getTotalAutoPrice()).isEqualTo(0.0);
    }

    @Test
    void totalAviaPriceSumsTakeAviaDeliver() {
        CarrierRateResponse r = new CarrierRateResponse();
        r.setTake(List.of("", "", "200"));
        r.setAvia(List.of("", "", "1800"));
        r.setDeliver(List.of("", "", "100"));

        assertThat(r.getTotalAviaPrice()).isEqualTo(2100.0);
    }

    @Test
    void autoDaysExtractsFirstNumberFromPeriodsString() {
        CarrierRateResponse r = new CarrierRateResponse();
        r.setPeriods("Срок: 3-5 дней");
        assertThat(r.getAutoDays()).isEqualTo(3);
    }

    @Test
    void autoDaysReturnsZeroWhenPeriodsIsNull() {
        assertThat(new CarrierRateResponse().getAutoDays()).isZero();
    }

    @Test
    void autoDaysReturnsZeroWhenPeriodsIsBlank() {
        CarrierRateResponse r = new CarrierRateResponse();
        r.setPeriods("   ");
        assertThat(r.getAutoDays()).isZero();
    }

    @Test
    void autoDaysReturnsZeroWhenNoDigits() {
        CarrierRateResponse r = new CarrierRateResponse();
        r.setPeriods("Срок неизвестен");
        assertThat(r.getAutoDays()).isZero();
    }

    @Test
    void autoDaysHandlesSingleNumber() {
        CarrierRateResponse r = new CarrierRateResponse();
        r.setPeriods("7");
        assertThat(r.getAutoDays()).isEqualTo(7);
    }
}
