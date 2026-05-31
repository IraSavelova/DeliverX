package com.deliverx.rates_service.dto.carrier;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DellinCalculatorResponseTest {

    @Test
    void hasDataReturnsFalseForEmptyResponse() {
        assertThat(new DellinCalculatorResponse().hasData()).isFalse();
    }

    @Test
    void hasDataReturnsTrueWhenDataPresent() {
        DellinCalculatorResponse r = new DellinCalculatorResponse();
        r.setData(new DellinCalculatorResponse.Data());

        assertThat(r.hasData()).isTrue();
    }

    @Test
    void autoPriceReturnsZeroWhenPriceIsNull() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();

        assertThat(d.getAutoPrice()).isZero();
    }

    @Test
    void autoPriceReturnsRawPriceValue() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        d.setPrice(1500.5);

        assertThat(d.getAutoPrice()).isEqualTo(1500.5);
    }

    @Test
    void aviaPriceReturnsZeroIfAvailableTypesIsNull() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        d.setPrice(1000.0);

        assertThat(d.getAviaPrice()).isZero();
    }

    @Test
    void aviaPriceReturnsZeroIfAviaIsMissing() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        d.setPrice(1000.0);
        d.setAvailableDeliveryTypes(Map.of("auto", 500.0));

        assertThat(d.getAviaPrice()).isZero();
    }

    @Test
    void aviaPriceComputedAsAutoPlusDifference() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        d.setPrice(2000.0);
        d.setAvailableDeliveryTypes(Map.of("auto", 800.0, "avia", 1500.0));

        // 2000 + (1500 - 800) = 2700
        assertThat(d.getAviaPrice()).isEqualTo(2700.0);
    }

    @Test
    void aviaPriceZeroIfAutoIntercityIsZero() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        d.setPrice(2000.0);
        d.setAvailableDeliveryTypes(Map.of("auto", 0.0, "avia", 1500.0));

        assertThat(d.getAviaPrice()).isZero();
    }

    @Test
    void expressPriceMirrorsAviaLogic() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        d.setPrice(2000.0);
        d.setAvailableDeliveryTypes(Map.of("auto", 800.0, "express", 1100.0));

        // 2000 + (1100 - 800) = 2300
        assertThat(d.getExpressPrice()).isEqualTo(2300.0);
    }

    @Test
    void expressPriceZeroIfExpressMissing() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        d.setPrice(2000.0);
        d.setAvailableDeliveryTypes(Map.of("auto", 800.0));

        assertThat(d.getExpressPrice()).isZero();
    }

    @Test
    void daysFromDeliveryTermWhenPositive() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        d.setDeliveryTerm(7);

        assertThat(d.getDays()).isEqualTo(7);
    }

    @Test
    void daysFromOrderDatesUsingGiveoutWhenDeliveryTermIsZero() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        d.setDeliveryTerm(0);
        DellinCalculatorResponse.OrderDates dates = new DellinCalculatorResponse.OrderDates();
        dates.setPickup("2026-05-01");
        dates.setGiveoutFromOspReceiver("2026-05-08 10:00:00");
        d.setOrderDates(dates);

        assertThat(d.getDays()).isEqualTo(7);
    }

    @Test
    void daysFromOrderDatesUsingArrivalIfGiveoutMissing() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        DellinCalculatorResponse.OrderDates dates = new DellinCalculatorResponse.OrderDates();
        dates.setPickup("2026-05-01");
        dates.setArrivalToOspReceiver("2026-05-05");
        d.setOrderDates(dates);

        assertThat(d.getDays()).isEqualTo(4);
    }

    @Test
    void daysZeroIfOrderDatesNullAndNoTerm() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();

        assertThat(d.getDays()).isZero();
    }

    @Test
    void daysZeroIfPickupCannotBeParsed() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        DellinCalculatorResponse.OrderDates dates = new DellinCalculatorResponse.OrderDates();
        dates.setPickup("not-a-date");
        dates.setArrivalToOspReceiver("2026-05-08");
        d.setOrderDates(dates);

        assertThat(d.getDays()).isZero();
    }

    @Test
    void daysZeroIfPickupPresentButArrivalMissing() {
        DellinCalculatorResponse.Data d = new DellinCalculatorResponse.Data();
        DellinCalculatorResponse.OrderDates dates = new DellinCalculatorResponse.OrderDates();
        dates.setPickup("2026-05-01");
        d.setOrderDates(dates);

        assertThat(d.getDays()).isZero();
    }
}
