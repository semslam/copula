package com.copulaapp.webservice.util;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.ws.rs.core.Response;

/**
 * Created by heeleeaz on 7/4/16.
 */
public class RP {
    public static final int SUCCESS = 1, EXISTS = 2;
    public static final int EMPTY = 0, WRONG_INPUT = -2;
    public static final int FAILED = -1;

    private int statusCode;
    private String statusMessage, nextPageToken;
    private Object result;


    private RP() {
    }

    public static Response pager(int status, String msg, Pager pager) {
        Builder builder = new Builder();
        builder.status(status).message(msg).nextPage(pager.getNextPageToken());
        builder.result(pager.packet);

        return Response.status(200).entity(builder.build()).build();
    }

    public static Response string(int status, String msg) {
        Builder builder = new Builder();
        builder.status(status).message(msg);
        return Response.status(200).entity(builder.build()).build();
    }


    public static Response string(int status, String msg, String entity) {
        Builder builder = new Builder();
        builder.status(status).message(msg).result(entity);
        return Response.status(200).entity(builder.build()).build();
    }

    public static Response object(int status, String msg, Object entity) {
        Builder builder = new Builder();
        builder.status(status).message(msg).result(entity);
        return Response.status(200).entity(builder.build()).build();
    }


    private String build() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("statusCode", statusCode).add("statusMessage", statusMessage);
        if (nextPageToken != null) builder.add("nextPageToken", nextPageToken);

        if (result != null && result instanceof JsonValue) {
            builder.add("result", (JsonValue) result);
        } else builder.add("result", String.valueOf(result));

        return builder.build().toString();
    }


    private static class Builder {
        private RP mResponse = new RP();

        Builder status(int code) {
            mResponse.statusCode = code;
            return this;
        }

        Builder message(String message) {
            mResponse.statusMessage = message;
            return this;
        }

        Builder nextPage(String nextPage) {
            mResponse.nextPageToken = nextPage;
            return this;
        }

        Builder result(Object result) {
            mResponse.result = ObjectJsonMapper.toJson(result);
            return this;
        }

        Builder result(String result) {
            mResponse.result = result;
            return this;
        }

        String build() {
            return mResponse.build();
        }
    }
}
