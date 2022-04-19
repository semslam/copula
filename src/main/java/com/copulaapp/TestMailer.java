package com.copulaapp;

import com.copulaapp.messaging.mailer.MailDispatcher;
import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.models.feed.FeedEntry;
import com.copulaapp.webservice.models.feed.notification.ExploreAnnouncer;

import static com.copulaapp.webservice.Copula.MYSQL_PASSWORD;
import static com.copulaapp.webservice.Copula.MYSQL_USER;

/**
 * Demo app that shows how to construct and send a single part html
 * message.  Note that the same basic technique can be used to send
 * data of any type.
 *
 * @author John Mani
 * @author Bill Shannon
 * @author Max Spivak
 */

public class TestMailer {

    public static void main(String[] argv) throws Exception {
        CP30MysqlPooling.initialize(MYSQL_USER, MYSQL_PASSWORD);
        MailDispatcher.getInstance().startDispatcher();

        ExploreAnnouncer exploreAnnouncer = new ExploreAnnouncer();

        FeedEntry f = new FeedEntry();
        f.posterUsername = "heeleeaz";
        f.mediaCaption = "shsdihcdubcu hu dschds vcdsuhuh ud";
        f.mediaTitle = "dfdjfdji";
        f.posterId = "Asdis";
        //exploreAnnouncer.sendNotificationAsync(f);
    }

}
