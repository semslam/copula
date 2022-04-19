package com.copulaapp.messaging.cloudmessage.http;

import com.copulaapp.messaging.cloudmessage.xmpp.CCSClient;
import com.copulaapp.messaging.cloudmessage.xmpp.CCSOutMessage;
import com.copulaapp.messaging.cloudmessage.xmpp.MessageHelper;
import com.copulaapp.messaging.cloudmessage.xmpp.Util;

import java.util.Map;

public class FCMMessaging {
    private static FCMMessaging sInstance;
    private CCSClient ccsClient;

    private FCMMessaging(String senderId, String serverKey) {
        this.ccsClient = CCSClient.prepareClient(senderId, serverKey);
    }

    public static void
    fcmInitialize(String senderId, String serverKey) throws Exception {
        if (sInstance == null) {
            sInstance = new FCMMessaging(senderId, serverKey);
            sInstance.connect();
        }
    }

    public static FCMMessaging getInstance() {
        return sInstance;
    }

    private void connect() throws Exception {
        this.ccsClient.connect();
    }

    public void sendData(String to, Map<String, String> n, Map<String, String> data) {
        String messageId = Util.getUniqueMessageId();
        CCSOutMessage message = new CCSOutMessage(to, messageId);
        message.setDataPayload(data);
        message.setNotificationPayload(n);

        String jsonRequest = MessageHelper.createJsonOutMessage(message);
        ccsClient.send(jsonRequest);
    }
}