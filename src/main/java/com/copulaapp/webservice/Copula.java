package com.copulaapp.webservice;

import com.copulaapp.messaging.cloudmessage.FCM;
import com.copulaapp.messaging.cloudmessage.http.FCMMessaging;
import com.copulaapp.messaging.mailer.MailDispatcher;
import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.filter.AuthenticationFilter;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by heeleeaz on 6/29/16.
 */
@ApplicationPath("/")
public class Copula extends ResourceConfig {
    public static final String REDIS_NODE = "localhost";
    public static final String MYSQL_USER = "root";
    public static final String MYSQL_PASSWORD = "root";

    public Copula() {
        register(LoggingFeature.class).register(MultiPartFeature.class)
                .register(AuthenticationFilter.class);

        packages("com.copulaapp.webservice.controllers",
                "com.copulaapp.mediaws.controllers",
                "com.copulaapp.webservice.exception");

        try {
            CP30MysqlPooling.initialize(MYSQL_USER, MYSQL_PASSWORD);
            FCMMessaging.fcmInitialize(FCM.SENDER_ID, FCM.SERVER_KEY);
            MailDispatcher.getInstance().startDispatcher();
        } catch (Exception e) {
            Logger.getLogger("Copula").log(Level.SEVERE, e.getMessage());
        }
    }//END
}
