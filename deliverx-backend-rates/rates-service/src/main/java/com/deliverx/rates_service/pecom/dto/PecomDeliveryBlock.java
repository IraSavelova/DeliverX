package com.deliverx.rates_service.pecom.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class PecomDeliveryBlock {

    private String name;
    private String description;
    private Double price;

    public PecomDeliveryBlock() {
    }

    public PecomDeliveryBlock(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public static PecomDeliveryBlock fromRawList(List<Object> raw) {
        if (raw == null || raw.size() < 3) {
            return new PecomDeliveryBlock(null, null, 0.0);
        }

        String name = raw.get(0) == null ? null : raw.get(0).toString();
        String description = raw.get(1) == null ? null : raw.get(1).toString();
        Double price = parsePrice(raw.get(2));

        return new PecomDeliveryBlock(name, description, price);
    }

    private static Double parsePrice(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        try {
            return Double.parseDouble(value.toString().replace(",", ".").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }
}