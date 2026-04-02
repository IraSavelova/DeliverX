package com.deliverx.rates_service.pecom.dto;

import java.util.ArrayList;
import java.util.List;

public class PecomRequestParams {

    private Integer takeTownId;
    private Integer deliverTownId;
    private List<PecomPlace> places = new ArrayList<>();

    public Integer getTakeTownId() {
        return takeTownId;
    }

    public void setTakeTownId(Integer takeTownId) {
        this.takeTownId = takeTownId;
    }

    public Integer getDeliverTownId() {
        return deliverTownId;
    }

    public void setDeliverTownId(Integer deliverTownId) {
        this.deliverTownId = deliverTownId;
    }

    public List<PecomPlace> getPlaces() {
        return places;
    }

    public void setPlaces(List<PecomPlace> places) {
        this.places = places;
    }
}