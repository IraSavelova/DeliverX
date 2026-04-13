package com.deliverx.rates_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class RateRequest {

    @NotBlank(message = "fromCity is required")
    private String fromCity;

    @NotBlank(message = "toCity is required")
    private String toCity;

    @Positive(message = "weightKg must be positive")
    private Double weightKg;

    @Positive(message = "lengthCm must be positive")
    private Integer lengthCm;

    @Positive(message = "widthCm must be positive")
    private Integer widthCm;

    @Positive(message = "heightCm must be positive")
    private Integer heightCm;

    private String deliverySpeed;

    public String getFromCity() { return fromCity; }
    public void setFromCity(String fromCity) { this.fromCity = fromCity; }

    public String getToCity() { return toCity; }
    public void setToCity(String toCity) { this.toCity = toCity; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Integer getLengthCm() { return lengthCm; }
    public void setLengthCm(Integer lengthCm) { this.lengthCm = lengthCm; }

    public Integer getWidthCm() { return widthCm; }
    public void setWidthCm(Integer widthCm) { this.widthCm = widthCm; }

    public Integer getHeightCm() { return heightCm; }
    public void setHeightCm(Integer heightCm) { this.heightCm = heightCm; }

    public String getDeliverySpeed() { return deliverySpeed; }
    public void setDeliverySpeed(String deliverySpeed) { this.deliverySpeed = deliverySpeed; }
}
