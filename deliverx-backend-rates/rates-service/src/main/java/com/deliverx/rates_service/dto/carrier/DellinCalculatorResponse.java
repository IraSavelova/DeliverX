package com.deliverx.rates_service.dto.carrier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Ответ калькулятора Деловых Линий v2.
 *
 * Ключевые поля:
 *   data.price                  — итоговая стоимость (авто)
 *   data.availableDeliveryTypes — цены по типам: auto, small, avia, express
 *   data.deliveryTerm           — срок доставки в днях
 *   data.orderDates.giveoutFromOspReceiver — дата выдачи получателю
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DellinCalculatorResponse {

    @JsonProperty("data")
    private Data data;

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    public boolean hasData() {
        return data != null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {

        // Итоговая стоимость авто (основной тариф)
        @JsonProperty("price")
        private Double price;

        // Срок доставки в днях
        @JsonProperty("deliveryTerm")
        private Integer deliveryTerm;

        // Цены по типам доставки: auto, small, avia, express
        @JsonProperty("availableDeliveryTypes")
        private Map<String, Double> availableDeliveryTypes;

        @JsonProperty("orderDates")
        private OrderDates orderDates;

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Integer getDeliveryTerm() { return deliveryTerm; }
        public void setDeliveryTerm(Integer deliveryTerm) { this.deliveryTerm = deliveryTerm; }

        public Map<String, Double> getAvailableDeliveryTypes() { return availableDeliveryTypes; }
        public void setAvailableDeliveryTypes(Map<String, Double> v) { this.availableDeliveryTypes = v; }

        public OrderDates getOrderDates() { return orderDates; }
        public void setOrderDates(OrderDates orderDates) { this.orderDates = orderDates; }

        /** Цена авто-доставки — полная с забором и доставкой. */
        public double getAutoPrice() {
            // data.price — итоговая стоимость для priceMinimal типа (обычно auto)
            // availableDeliveryTypes.auto — только межгород без забора/доставки
            // Используем полную цену data.price для авто
            return price != null ? price : 0.0;
        }

        /** Цена авиа. Считаем пропорционально: берём разницу авиа/авто из availableDeliveryTypes
         *  и добавляем к итоговой цене. */
        public double getAviaPrice() {
            if (availableDeliveryTypes == null) return 0.0;
            Double aviaIntercity = availableDeliveryTypes.get("avia");
            Double autoIntercity = availableDeliveryTypes.get("auto");
            if (aviaIntercity == null || aviaIntercity <= 0) return 0.0;
            if (autoIntercity == null || autoIntercity <= 0 || price == null) return 0.0;
            // итоговая цена авиа = data.price + (avia - auto)
            return price + (aviaIntercity - autoIntercity);
        }

        /** Цена экспресс. Аналогично авиа. */
        public double getExpressPrice() {
            if (availableDeliveryTypes == null) return 0.0;
            Double expressIntercity = availableDeliveryTypes.get("express");
            Double autoIntercity    = availableDeliveryTypes.get("auto");
            if (expressIntercity == null || expressIntercity <= 0) return 0.0;
            if (autoIntercity == null || autoIntercity <= 0 || price == null) return 0.0;
            return price + (expressIntercity - autoIntercity);
        }

        /** Срок в днях. Считаем по датам если deliveryTerm = 0. */
        public int getDays() {
            if (deliveryTerm != null && deliveryTerm > 0) return deliveryTerm;
            if (orderDates == null || orderDates.getPickup() == null) return 0;
            try {
                java.time.LocalDate pickup = java.time.LocalDate.parse(orderDates.getPickup());
                // Приоритет дат: выдача получателю → прибытие в ОСП получателя
                String arrivalDateStr = orderDates.getGiveoutFromOspReceiver() != null
                        ? orderDates.getGiveoutFromOspReceiver().substring(0, 10)
                        : orderDates.getArrivalToOspReceiver();
                if (arrivalDateStr == null) return 0;
                java.time.LocalDate arrival = java.time.LocalDate.parse(arrivalDateStr);
                return (int) java.time.temporal.ChronoUnit.DAYS.between(pickup, arrival);
            } catch (Exception ignored) {
                return 0;
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderDates {

        @JsonProperty("pickup")
        private String pickup;

        @JsonProperty("arrivalToOspReceiver")
        private String arrivalToOspReceiver; // "2026-04-18"

        @JsonProperty("giveoutFromOspReceiver")
        private String giveoutFromOspReceiver; // "2019-11-28 00:00:00"

        @JsonProperty("giveoutFromOspReceiverMax")
        private String giveoutFromOspReceiverMax;

        public String getPickup() { return pickup; }
        public void setPickup(String pickup) { this.pickup = pickup; }

        public String getArrivalToOspReceiver() { return arrivalToOspReceiver; }
        public void setArrivalToOspReceiver(String v) { this.arrivalToOspReceiver = v; }

        public String getGiveoutFromOspReceiver() { return giveoutFromOspReceiver; }
        public void setGiveoutFromOspReceiver(String v) { this.giveoutFromOspReceiver = v; }

        public String getGiveoutFromOspReceiverMax() { return giveoutFromOspReceiverMax; }
        public void setGiveoutFromOspReceiverMax(String v) { this.giveoutFromOspReceiverMax = v; }
    }
}
