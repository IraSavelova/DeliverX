package com.deliverx.rates_service.dto.carrier;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressRuCalculatorResponseTest {

    @Test
    void isOkTrueWhenErrorFalseAndResultPresent() {
        ExpressRuCalculatorResponse r = new ExpressRuCalculatorResponse();
        r.setStatus("ok");
        r.setError(false);
        r.setResult(List.of(new ExpressRuCalculatorResponse.Tariff()));

        assertThat(r.isOk()).isTrue();
    }

    @Test
    void isOkFalseWhenErrorTrue() {
        ExpressRuCalculatorResponse r = new ExpressRuCalculatorResponse();
        r.setError(true);
        r.setResult(List.of(new ExpressRuCalculatorResponse.Tariff()));

        assertThat(r.isOk()).isFalse();
    }

    @Test
    void isOkFalseWhenResultNull() {
        ExpressRuCalculatorResponse r = new ExpressRuCalculatorResponse();
        r.setError(false);

        assertThat(r.isOk()).isFalse();
    }

    @Test
    void isOkFalseWhenResultEmpty() {
        ExpressRuCalculatorResponse r = new ExpressRuCalculatorResponse();
        r.setError(false);
        r.setResult(List.of());

        assertThat(r.isOk()).isFalse();
    }

    @Test
    void isOkFalseWhenErrorIsNull() {
        ExpressRuCalculatorResponse r = new ExpressRuCalculatorResponse();
        r.setResult(List.of(new ExpressRuCalculatorResponse.Tariff()));
        // error = null

        assertThat(r.isOk()).isFalse();
    }

    @Test
    void estimatedDaysPrefersDayTo() {
        ExpressRuCalculatorResponse.Tariff t = new ExpressRuCalculatorResponse.Tariff();
        t.setDayFrom(1);
        t.setDayTo(3);

        assertThat(t.getEstimatedDays()).isEqualTo(3);
    }

    @Test
    void estimatedDaysFallsBackToDayFrom() {
        ExpressRuCalculatorResponse.Tariff t = new ExpressRuCalculatorResponse.Tariff();
        t.setDayFrom(2);
        t.setDayTo(0);

        assertThat(t.getEstimatedDays()).isEqualTo(2);
    }

    @Test
    void estimatedDaysReturns1WhenBothZero() {
        ExpressRuCalculatorResponse.Tariff t = new ExpressRuCalculatorResponse.Tariff();
        t.setDayFrom(0);
        t.setDayTo(0);

        // dayFrom=dayTo=0 в документации означает "на следующий день"
        assertThat(t.getEstimatedDays()).isEqualTo(1);
    }

    @Test
    void estimatedDaysReturns1WhenBothNull() {
        ExpressRuCalculatorResponse.Tariff t = new ExpressRuCalculatorResponse.Tariff();

        assertThat(t.getEstimatedDays()).isEqualTo(1);
    }

    @Test
    void estimatedDaysParsesStringValues() {
        // В документации поля строки, в реальности — числа. Должны работать оба.
        ExpressRuCalculatorResponse.Tariff t = new ExpressRuCalculatorResponse.Tariff();
        t.setDayFrom("2");
        t.setDayTo("5");

        assertThat(t.getEstimatedDays()).isEqualTo(5);
    }

    @Test
    void estimatedDaysParsesIntegerValues() {
        ExpressRuCalculatorResponse.Tariff t = new ExpressRuCalculatorResponse.Tariff();
        t.setDayFrom(2);
        t.setDayTo(5);

        assertThat(t.getEstimatedDays()).isEqualTo(5);
    }

    @Test
    void estimatedDaysReturns1ForUnparsableStrings() {
        ExpressRuCalculatorResponse.Tariff t = new ExpressRuCalculatorResponse.Tariff();
        t.setDayFrom("garbage");
        t.setDayTo("also garbage");

        // оба не парсятся → 0 → fallback → 1
        assertThat(t.getEstimatedDays()).isEqualTo(1);
    }

    @Test
    void estimatedDaysHandlesEmptyString() {
        ExpressRuCalculatorResponse.Tariff t = new ExpressRuCalculatorResponse.Tariff();
        t.setDayFrom("");
        t.setDayTo("");

        assertThat(t.getEstimatedDays()).isEqualTo(1);
    }

    @Test
    void estimatedDaysHandlesDoubleValues() {
        // Number от Jackson может прийти как Double
        ExpressRuCalculatorResponse.Tariff t = new ExpressRuCalculatorResponse.Tariff();
        t.setDayFrom(2.0);
        t.setDayTo(5.0);

        assertThat(t.getEstimatedDays()).isEqualTo(5);
    }
}
