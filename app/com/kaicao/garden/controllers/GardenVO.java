package com.kaicao.garden.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaicao.garden.models.Garden;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Date;

/**
 * Created by kaicao on 26/10/14.
 */
public class GardenVO {

    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private Date date;
    @JsonProperty
    private int value;

    public GardenVO() {
    }

    public GardenVO(Garden garden) throws Exception {
        BeanUtils.copyProperties(this, garden);
    }

    @JsonCreator
    public GardenVO(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("date") Date date,
            @JsonProperty("value") int value) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.value = value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public int getValue() {
        return value;
    }
}
