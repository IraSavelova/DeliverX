package com.deliverx.rates_service.dto.carrier;

/**
 * Параметры GET-запроса к PEK API калькулятору.
 * http://calc.pecom.ru/bitrix/components/pecom/calc/ajax.php
 *
 * PEK принимает параметры груза в виде массивов:
 *   places[0][]: ширина, длина, высота, объём, вес, негабарит, ЗТУ
 * Размеры передаём в метрах (конвертируем из см), вес в кг.
 */
public class CarrierRateRequest {

    // ID города отправки (берём из /ru/calc/towns.php)
    private String takeTown;

    // ID города доставки
    private String deliverTown;

    // Параметры груза (уже сконвертированные в метры)
    private double widthM;
    private double lengthM;
    private double heightM;
    private double weightKg;

    public CarrierRateRequest() {}

    public CarrierRateRequest(String takeTown, String deliverTown,
                               double widthM, double lengthM, double heightM, double weightKg) {
        this.takeTown = takeTown;
        this.deliverTown = deliverTown;
        this.widthM = widthM;
        this.lengthM = lengthM;
        this.heightM = heightM;
        this.weightKg = weightKg;
    }

    public String getTakeTown() { return takeTown; }
    public void setTakeTown(String takeTown) { this.takeTown = takeTown; }

    public String getDeliverTown() { return deliverTown; }
    public void setDeliverTown(String deliverTown) { this.deliverTown = deliverTown; }

    public double getWidthM() { return widthM; }
    public void setWidthM(double widthM) { this.widthM = widthM; }

    public double getLengthM() { return lengthM; }
    public void setLengthM(double lengthM) { this.lengthM = lengthM; }

    public double getHeightM() { return heightM; }
    public void setHeightM(double heightM) { this.heightM = heightM; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
}
