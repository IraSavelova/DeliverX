package com.deliverx.rates_service.dto;

public class RateResponse {

    private String carrier;
    private Double price;
    private Integer estimatedDays;
    private String deliveryMethod;

    public RateResponse() {
    }

    public RateResponse(String carrier, Double price, Integer estimatedDays, String deliveryMethod) {
        this.carrier = carrier;
        this.price = price;
        this.estimatedDays = estimatedDays;
        this.deliveryMethod = deliveryMethod;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getEstimatedDays() {
        return estimatedDays;
    }

    public void setEstimatedDays(Integer estimatedDays) {
        this.estimatedDays = estimatedDays;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
}
