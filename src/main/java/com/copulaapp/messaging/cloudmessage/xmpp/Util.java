package com.copulaapp.messaging.cloudmessage.xmpp;

import java.util.UUID;

/**
 * Util class for constants and generic methods
 */

public class Util {

    // For the GCM connection
    public static final String FCM_SERVER = "gcm.googleapis.com";
    public static final int FCM_PORT = 5235;
    public static final String FCM_ELEMENT_NAME = "gcm";
    public static final String FCM_NAMESPACE = "google:mobile:data";
    public static final String FCM_SERVER_CONNECTION = "gcm.googleapis.com";

    // For the processor factory
    public static final String PACKAGE = "com.copulaapp";
    public static final String BACKEND_ACTION_REGISTER = PACKAGE + ".REGISTER";
    public static final String BACKEND_ACTION_ECHO = PACKAGE + ".ECHO";
    public static final String BACKEND_ACTION_MESSAGE = PACKAGE + ".PAYLOAD_ACTION";


    public static final String PAYLOAD_TITLE = "title";
    public static final String PAYLOAD_BODY = "body";
    public static final String PAYLOAD_CLICK_ACTION = "click_action";
    public static final String PAYLOAD_LINK = "link";
    public static final String PAYLOAD_IMAGE = "image";


    /**
     * Returns a random message id to uniquely identify a message
     */
    public static String getUniqueMessageId() {
        return "m-" + UUID.randomUUID().toString();
    }

}
