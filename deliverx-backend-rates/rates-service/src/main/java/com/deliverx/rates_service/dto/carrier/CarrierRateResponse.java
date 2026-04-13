package com.deliverx.rates_service.dto.carrier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Ответ от PEK публичного API калькулятора.
 *
 * Каждое поле — массив из трёх элементов: [название услуги, пояснение, стоимость].
 * Если ключа нет — услуга не предоставляется на этом направлении.
 *
 * Итоговая стоимость авто = take[2] + auto[2] + deliver[2]
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarrierRateResponse {

    // Забор груза: [название, описание, стоимость]
    @JsonProperty("take")
    private List<String> take;

    // Автоперевозка
    @JsonProperty("auto")
    private List<String> auto;

    // Авиаперевозка
    @JsonProperty("avia")
    private List<String> avia;

    // Доставка до получателя
    @JsonProperty("deliver")
    private List<String> deliver;

    // Сроки автоперевозки
    @JsonProperty("periods")
    private String periods;

    // Сроки авиаперевозки
    @JsonProperty("aperiods")
    private String aperiods;

    // Список ошибок
    @JsonProperty("error")
    private List<String> error;

    public CarrierRateResponse() {}

    /**
     * Есть ли автоперевозка на этом направлении.
     */
    public boolean hasAuto() {
        return auto != null && auto.size() >= 3;
    }

    /**
     * Есть ли авиаперевозка на этом направлении.
     */
    public boolean hasAvia() {
        return avia != null && avia.size() >= 3;
    }

    /**
     * Суммарная стоимость автоперевозки: забор + авто + доставка.
     * PEK кладёт стоимость в элемент с индексом 2.
     */
    public double getTotalAutoPrice() {
        return parsePrice(take) + parsePrice(auto) + parsePrice(deliver);
    }

    /**
     * Суммарная стоимость авиаперевозки: забор + авиа + доставка.
     */
    public double getTotalAviaPrice() {
        return parsePrice(take) + parsePrice(avia) + parsePrice(deliver);
    }

    /**
     * Срок автоперевозки в днях (парсим из строки "periods").
     * PEK пишет что-то вроде "Срок доставки: 3-5 дней" — берём первое число.
     */
    public int getAutoDays() {
        if (periods == null || periods.isBlank()) return 0;
        try {
            // ищем первое число в строке
            String digits = periods.replaceAll("[^0-9]", " ").trim().split("\\s+")[0];
            return Integer.parseInt(digits);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Парсим стоимость из элемента массива [name, desc, price].
     * PEK передаёт цену строкой, иногда с пробелами: "1 290.00"
     */
    private double parsePrice(List<String> arr) {
        if (arr == null || arr.size() < 3) return 0.0;
        try {
            return Double.parseDouble(arr.get(2).replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public List<String> getTake() { return take; }
    public void setTake(List<String> take) { this.take = take; }

    public List<String> getAuto() { return auto; }
    public void setAuto(List<String> auto) { this.auto = auto; }

    public List<String> getAvia() { return avia; }
    public void setAvia(List<String> avia) { this.avia = avia; }

    public List<String> getDeliver() { return deliver; }
    public void setDeliver(List<String> deliver) { this.deliver = deliver; }

    public String getPeriods() { return periods; }
    public void setPeriods(String periods) { this.periods = periods; }

    public String getAperiods() { return aperiods; }
    public void setAperiods(String aperiods) { this.aperiods = aperiods; }

    public List<String> getError() { return error; }
    public void setError(List<String> error) { this.error = error; }
}
