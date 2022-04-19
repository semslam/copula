package com.copulaapp.messaging.cloudmessage.http;

import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by heeleaz on 7/6/17.
 */
public class FCMAdmin {
    private OkHttpClient connection;
    private String mAuthorizationKey;

    public FCMAdmin(String authorizationKey) {
        this.mAuthorizationKey = authorizationKey;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10000, TimeUnit.MILLISECONDS);
        connection = builder.build();
    }

    public void setAuthorizationKey(String key) {
        mAuthorizationKey = key;
    }

    public boolean subscribeToTopic(String topic, String token) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        Request.Builder builder = new Request.Builder();
        RequestBody params = RequestBody.create(JSON, "");
        String url = "https://iid.googleapis.com/iid/v1/" + token + "/rel/topics/" + topic;
        builder.url(url).method("POST", params);
        builder.addHeader("Authorization", "key=" + mAuthorizationKey);

        try {
            Response response = connection.newCall(builder.build()).execute();
            System.out.print(response.body().string());
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    private boolean relationshipMap(String url, String topic, List<String> tokens)
            throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request.Builder builder = new Request.Builder();

        JSONObject body = new JSONObject();
        body.put("registration_tokens", JSONArray.toJSONString(tokens));
        body.put("to", "/topics/" + topic);

        RequestBody jsonBody = RequestBody.create(JSON, body.toJSONString());
        builder.url(url).post(jsonBody);
        builder.addHeader("Authorization", "key=" + mAuthorizationKey);
        Response response = connection.newCall(builder.build()).execute();
        return response.isSuccessful();
    }

    public boolean subcribeToTopic(String topic, List<String> tokens) {
        String url = "https://iid.googleapis.com/iid/v1:batchAdd";
        try {
            return relationshipMap(url, topic, tokens);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean unSubcribeFromTopic(String topic, List<String> tokens) {
        String url = "https://iid.googleapis.com/iid/v1:batchRemove";
        try {
            return relationshipMap(url, topic, tokens);
        } catch (Exception e) {
            return false;
        }
    }
}
