package com.copulaapp.messaging.cloudmessage.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;

import javax.net.ssl.SSLSocketFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * * https://firebase.google.com/docs/cloud-messaging/xmpp-server-ref
 */
public class CCSClient {
    private static final Logger logger = Logger.getLogger(CCSClient.class.getName());
    private static CCSClient sInstance = null;

    private XMPPConnection connection;
    private String mApiKey = null;
    private String fcmServerUsername = null;

    private CCSClient(String projectId, String apiKey) {
        this();
        mApiKey = apiKey;
        fcmServerUsername = projectId + "@" + Util.FCM_SERVER_CONNECTION;
    }

    private CCSClient() {
        // Add GCMPacketExtension
        ProviderManager.getInstance().addExtensionProvider(Util.FCM_ELEMENT_NAME, Util.FCM_NAMESPACE,
                (PacketExtensionProvider) parser -> new GCMPacketExtension(parser.nextText()));
    }

    public static CCSClient getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("You have to prepare the client first");
        }
        return sInstance;
    }

    public static CCSClient prepareClient(String projectId, String apiKey) {
        synchronized (CCSClient.class) {
            if (sInstance == null) {
                sInstance = new CCSClient(projectId, apiKey);
            }
        }
        return sInstance;
    }

    /**
     * Connects to FCM Cloud Connection Server using the supplied credentials
     */
    public void connect() throws XMPPException {
        ConnectionConfiguration config =
                new ConnectionConfiguration(Util.FCM_SERVER, Util.FCM_PORT);
        config.setSecurityMode(SecurityMode.enabled);
        config.setReconnectionAllowed(true);
        config.setRosterLoadedAtLogin(false);
        config.setSendPresence(false);
        config.setSocketFactory(SSLSocketFactory.getDefault());

        connection = new XMPPConnection(config);
        connection.connect();

        // Log all outgoing packets
        connection.addPacketInterceptor(packet -> logger.log(Level.INFO,
                "Sent: {0}", packet.toXML()), new PacketTypeFilter(Message.class));

        connection.login(fcmServerUsername, mApiKey);
        logger.log(Level.INFO, "Logged in: " + fcmServerUsername);
    }

    /**
     * Sends a downstream message to FCM
     */
    public void send(String jsonRequest) {
        Packet request = new GCMPacketExtension(jsonRequest).toPacket();
        connection.sendPacket(request);
    }
}
