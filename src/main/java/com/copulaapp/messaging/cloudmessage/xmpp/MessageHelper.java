package com.copulaapp.messaging.cloudmessage.xmpp;

import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper for the transformation of JSON messages to attribute maps and vice
 * versa in the XMPP Server
 */

public class MessageHelper {

    /**
     * Creates a JSON from a FCM outgoing message attributes
     */
    public static String createJsonOutMessage(CCSOutMessage outMessage) {
        return createJsonMessage(createAttributeMap(outMessage));
    }

    static String createJsonMessage(Map<String, Object> jsonMap) {
        return JSONValue.toJSONString(jsonMap);
    }

    /**
     * Creates a MAP from a FCM outgoing message attributes
     */
    static Map<String, Object> createAttributeMap(CCSOutMessage msg) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (msg.getTo() != null) map.put("to", msg.getTo());
        if (msg.getMessageId() != null) map.put("message_id", msg.getMessageId());
        map.put("data", msg.getDataPayload());

        if (msg.getCondition() != null) map.put("condition", msg.getCondition());
        if (msg.getCollapseKey() != null) map.put("collapse_key", msg.getCollapseKey());
        if (msg.getPriority() != null) map.put("priority", msg.getPriority());

        if (msg.isContentAvailable() != null && msg.isContentAvailable())
            map.put("content_available", true);
        if (msg.getTimeToLive() != null)
            map.put("time_to_live", msg.getTimeToLive());

        if (msg.isDeliveryReceiptRequested() != null && msg.isDeliveryReceiptRequested())
            map.put("delivery_receipt_requested", true);

        if (msg.isDryRun() != null && msg.isDryRun()) map.put("dry_run", true);

        if (msg.getNotificationPayload() != null) {
            map.put("notification", msg.getNotificationPayload());
        }
        return map;
    }
}
