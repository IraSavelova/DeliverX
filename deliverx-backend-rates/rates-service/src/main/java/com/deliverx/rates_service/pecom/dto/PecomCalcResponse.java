package com.deliverx.rates_service.pecom.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PecomCalcResponse {

    private List<Object> take;
    private List<Object> auto;
    private List<Object> alma_auto;
    private List<Object> avia;
    private List<Object> autonegabarit;
    private List<Object> ADD;
    private List<Object> ADD_1;
    private List<Object> ADD_2;
    private List<Object> ADD_3;
    private List<Object> ADD_4;
    private List<Object> deliver;
    private String periods;
    private String aperiods;
    private List<String> error;

    public List<Object> getTake() {
        return take;
    }

    public void setTake(List<Object> take) {
        this.take = take;
    }

    public List<Object> getAuto() {
        return auto;
    }

    public void setAuto(List<Object> auto) {
        this.auto = auto;
    }

    public List<Object> getAlma_auto() {
        return alma_auto;
    }

    public void setAlma_auto(List<Object> alma_auto) {
        this.alma_auto = alma_auto;
    }

    public List<Object> getAvia() {
        return avia;
    }

    public void setAvia(List<Object> avia) {
        this.avia = avia;
    }

    public List<Object> getAutonegabarit() {
        return autonegabarit;
    }

    public void setAutonegabarit(List<Object> autonegabarit) {
        this.autonegabarit = autonegabarit;
    }

    public List<Object> getADD() {
        return ADD;
    }

    public void setADD(List<Object> ADD) {
        this.ADD = ADD;
    }

    public List<Object> getADD_1() {
        return ADD_1;
    }

    public void setADD_1(List<Object> ADD_1) {
        this.ADD_1 = ADD_1;
    }

    public List<Object> getADD_2() {
        return ADD_2;
    }

    public void setADD_2(List<Object> ADD_2) {
        this.ADD_2 = ADD_2;
    }

    public List<Object> getADD_3() {
        return ADD_3;
    }

    public void setADD_3(List<Object> ADD_3) {
        this.ADD_3 = ADD_3;
    }

    public List<Object> getADD_4() {
        return ADD_4;
    }

    public void setADD_4(List<Object> ADD_4) {
        this.ADD_4 = ADD_4;
    }

    public List<Object> getDeliver() {
        return deliver;
    }

    public void setDeliver(List<Object> deliver) {
        this.deliver = deliver;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public String getAperiods() {
        return aperiods;
    }

    public void setAperiods(String aperiods) {
        this.aperiods = aperiods;
    }

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }
}