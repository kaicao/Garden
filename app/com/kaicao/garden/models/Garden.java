package com.kaicao.garden.models;

import com.mongodb.BasicDBObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by kaicao on 26/10/14.
 */
public class Garden extends BasicDBObject {

    public static Garden createGarden(String name, int value) {
        UUID uuid = UUID.randomUUID();
        return new Garden(uuid.toString(), name, new Date(), value);
    }

    public Garden() {
    }

    public Garden(String id, String name, Date date, int value) {
        put("id", id);
        put("name", name);
        put("date", date);
        put("value", value);
    }

    public void setName(String name) {
        put("name", name);
    }

    public void setDate(Date date) {
        put("date", date);
    }

    public void setValue(int value) {
        put("value", value);
    }

    public String getId() {
        return getString("id");
    }

    public String getName() {
        return getString("name");
    }

    public Date getDate() {
        return getDate("date");
    }

    public int getValue() {
        return getInt("value");
    }

}
