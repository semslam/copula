package com.copulaapp.webservice.util;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by heeleeaz on 8/12/16.
 */
public class Pager<T> {
    public static Pager NULL = new Pager(null, null);
    public List<T> packet;
    public String currentPageToken;

    public Pager() {
    }

    public Pager(List<T> packet, String currentPage) {
        this.currentPageToken = currentPage;
        this.packet = packet;
    }

    public String getNextPageToken() {
        String[] split = currentPageToken.split(",");
        int offset = Integer.valueOf(split[0].trim());
        int length = Integer.valueOf(split[1].trim());

        return (length + offset) + "," + length;
    }

    public String getCurrentPageToken() {
        return currentPageToken;
    }

    public List<T> getPacket() {
        return packet;
    }

    public void setPacket(List<T> packet) {
        this.packet = packet;
    }

    public String toJSONString() {
        try {
            JSONObject object = new JSONObject();
            object.put("result", ObjectJsonMapper.toJson(packet));
            object.put("currentPageToken", currentPageToken);
            return object.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
