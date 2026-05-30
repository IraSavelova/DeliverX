package com.deliverx.rates_service.dto.carrier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpressRuCalculatorResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("error")
    private Boolean error;

    @JsonProperty("result")
    private List<Tariff> result;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getError() { return error; }
    public void setError(Boolean error) { this.error = error; }

    public List<Tariff> getResult() { return result; }
    public void setResult(List<Tariff> result) { this.result = result; }

    public boolean isOk() {
        return Boolean.FALSE.equals(error) && result != null && !result.isEmpty();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tariff {

        @JsonProperty("name")
        private String name;

        @JsonProperty("typeLabel")
        private String typeLabel;

        @JsonProperty("rawPrice")
        private Double rawPrice;

        @JsonProperty("price")
        private String price;

        @JsonProperty("deliveryTime")
        private String deliveryTime;

        @JsonProperty("dayFrom")
        private Object dayFrom;

        @JsonProperty("dayTo")
        private Object dayTo;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getTypeLabel() { return typeLabel; }
        public void setTypeLabel(String typeLabel) { this.typeLabel = typeLabel; }

        public Double getRawPrice() { return rawPrice; }
        public void setRawPrice(Double rawPrice) { this.rawPrice = rawPrice; }

        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }

        public String getDeliveryTime() { return deliveryTime; }
        public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }

        public Object getDayFrom() { return dayFrom; }
        public void setDayFrom(Object dayFrom) { this.dayFrom = dayFrom; }

        public Object getDayTo() { return dayTo; }
        public void setDayTo(Object dayTo) { this.dayTo = dayTo; }

        public int getEstimatedDays() {
            int to   = parseInt(dayTo);
            int from = parseInt(dayFrom);
            if (to > 0)   return to;
            if (from > 0) return from;
            return 1;
        }

        private static int parseInt(Object o) {
            if (o == null) return 0;
            if (o instanceof Number) return ((Number) o).intValue();
            String s = o.toString().trim();
            if (s.isEmpty()) return 0;
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { return 0; }
        }
    }
}