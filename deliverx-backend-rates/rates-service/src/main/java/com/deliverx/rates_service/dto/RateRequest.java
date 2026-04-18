package com.deliverx.rates_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class RateRequest {

    @NotBlank(message = "fromCity is required")
    private String fromCity;

    // Улица и дом отправки — необязательно
    // Если указан — ДЛ считает курьерский забор до двери
    private String fromAddress;

    @NotBlank(message = "toCity is required")
    private String toCity;

    // Улица и дом доставки — необязательно
    private String toAddress;

    @Positive(message = "weightKg must be positive")
    private Double weightKg;

    @Positive(message = "lengthCm must be positive")
    private Integer lengthCm;

    @Positive(message = "widthCm must be positive")
    private Integer widthCm;

    @Positive(message = "heightCm must be positive")
    private Integer heightCm;

    private String deliverySpeed;

    // --- getters/setters ---

    public String getFromCity() { return fromCity; }
    public void setFromCity(String fromCity) { this.fromCity = fromCity; }

    public String getFromAddress() { return fromAddress; }
    public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }

    public String getToCity() { return toCity; }
    public void setToCity(String toCity) { this.toCity = toCity; }

    public String getToAddress() { return toAddress; }
    public void setToAddress(String toAddress) { this.toAddress = toAddress; }

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

    /** Полная строка для геокодинга: "Новосибирск, ул. Красный проспект, 1" или просто "Новосибирск" */
    public String getFullFromAddress() {
        if (fromAddress != null && !fromAddress.isBlank()) {
            return fromCity + ", " + fromAddress.trim();
        }
        return fromCity;
    }

    public String getFullToAddress() {
        if (toAddress != null && !toAddress.isBlank()) {
            return toCity + ", " + toAddress.trim();
        }
        return toCity;
    }

    /** Есть ли адрес отправки (для выбора варианта terminal vs address) */
    public boolean hasFromAddress() {
        return fromAddress != null && !fromAddress.isBlank();
    }

    public boolean hasToAddress() {
        return toAddress != null && !toAddress.isBlank();
    }
}
