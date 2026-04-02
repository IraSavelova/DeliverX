package com.deliverx.rates_service.pecom.dto;

import java.math.BigDecimal;

public class PecomPlace {

    private BigDecimal widthM;
    private BigDecimal lengthM;
    private BigDecimal heightM;
    private BigDecimal volumeM3;
    private BigDecimal weightKg;
    private Integer oversized;
    private Integer packageProtection;

    public BigDecimal getWidthM() {
        return widthM;
    }

    public void setWidthM(BigDecimal widthM) {
        this.widthM = widthM;
    }

    public BigDecimal getLengthM() {
        return lengthM;
    }

    public void setLengthM(BigDecimal lengthM) {
        this.lengthM = lengthM;
    }

    public BigDecimal getHeightM() {
        return heightM;
    }

    public void setHeightM(BigDecimal heightM) {
        this.heightM = heightM;
    }

    public BigDecimal getVolumeM3() {
        return volumeM3;
    }

    public void setVolumeM3(BigDecimal volumeM3) {
        this.volumeM3 = volumeM3;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public Integer getOversized() {
        return oversized;
    }

    public void setOversized(Integer oversized) {
        this.oversized = oversized;
    }

    public Integer getPackageProtection() {
        return packageProtection;
    }

    public void setPackageProtection(Integer packageProtection) {
        this.packageProtection = packageProtection;
    }
}