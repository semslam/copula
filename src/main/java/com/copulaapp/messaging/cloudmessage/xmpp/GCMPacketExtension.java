package com.copulaapp.messaging.cloudmessage.xmpp;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * XMPP Packet Extension for GCM Cloud Connection Server
 */
public class GCMPacketExtension extends DefaultPacketExtension {

    private String json;

    GCMPacketExtension(String json) {
        super(Util.FCM_ELEMENT_NAME, Util.FCM_NAMESPACE);
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    @Override
    public String toXML() {
        // TODO: Do we need to scape the json? StringUtils.escapeForXML(json)
        return String.format("<%s xmlns=\"%s\">%s</%s>",
                Util.FCM_ELEMENT_NAME, Util.FCM_NAMESPACE, json, Util.FCM_ELEMENT_NAME);
    }

    Packet toPacket() {
        Message message = new Message();
        message.addExtension(this);
        return message;
    }
}
