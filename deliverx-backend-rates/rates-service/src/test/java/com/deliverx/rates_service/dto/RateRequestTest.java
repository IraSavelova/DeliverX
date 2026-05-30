package com.deliverx.rates_service.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateRequestTest {

    @Test
    void fullFromAddressWithoutAddressReturnsCityOnly() {
        RateRequest r = new RateRequest();
        r.setFromCity("Москва");

        assertThat(r.getFullFromAddress()).isEqualTo("Москва");
    }

    @Test
    void fullFromAddressWithAddressJoinsThem() {
        RateRequest r = new RateRequest();
        r.setFromCity("Москва");
        r.setFromAddress("ул. Тверская, 1");

        assertThat(r.getFullFromAddress()).isEqualTo("Москва, ул. Тверская, 1");
    }

    @Test
    void fullFromAddressTrimsWhitespace() {
        RateRequest r = new RateRequest();
        r.setFromCity("Москва");
        r.setFromAddress("  ул. Тверская, 1  ");

        assertThat(r.getFullFromAddress()).isEqualTo("Москва, ул. Тверская, 1");
    }

    @Test
    void fullFromAddressWithBlankAddressReturnsCityOnly() {
        RateRequest r = new RateRequest();
        r.setFromCity("Москва");
        r.setFromAddress("   ");

        assertThat(r.getFullFromAddress()).isEqualTo("Москва");
    }

    @Test
    void fullToAddressMirrorsFromAddressBehavior() {
        RateRequest r = new RateRequest();
        r.setToCity("СПб");
        r.setToAddress("Невский, 1");

        assertThat(r.getFullToAddress()).isEqualTo("СПб, Невский, 1");
    }

    @Test
    void hasFromAddressIsTrueOnlyForNonBlankValue() {
        RateRequest r = new RateRequest();

        assertThat(r.hasFromAddress()).isFalse();   // null

        r.setFromAddress("");
        assertThat(r.hasFromAddress()).isFalse();

        r.setFromAddress("   ");
        assertThat(r.hasFromAddress()).isFalse();

        r.setFromAddress("ул. Ленина");
        assertThat(r.hasFromAddress()).isTrue();
    }

    @Test
    void hasToAddressIsTrueOnlyForNonBlankValue() {
        RateRequest r = new RateRequest();

        assertThat(r.hasToAddress()).isFalse();

        r.setToAddress("ул. Ленина");
        assertThat(r.hasToAddress()).isTrue();
    }
}
