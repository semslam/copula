package com.copulaapp;

import com.copulaapp.messaging.cloudmessage.FCM;
import com.copulaapp.messaging.cloudmessage.http.FCMMessaging;
import com.copulaapp.messaging.cloudmessage.xmpp.Util;
import com.copulaapp.webservice.models.feed.FeedEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by heeleaz on 7/26/17.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        String pnToken = "";
        //ProfileCloudMSGAware.subscribeAllAsync(pnToken);

        FCMMessaging.fcmInitialize(FCM.SENDER_ID, FCM.SERVER_KEY);

        FeedEntry feedEntry = new FeedEntry();
        feedEntry.posterId = "1";
        feedEntry.feedId = "22";
        feedEntry.posterUsername = "heeleeaz";
        feedEntry.mediaType = 1;
        feedEntry.mediaTitle = " asdljsaidjsi";
        feedEntry.thumbnailUrl = "https://copulaapp.s3.amazonaws.com/feeds/images/48.jpg";

        FCMMessaging fcmMessaging = FCMMessaging.getInstance();


        Map<String, String> notification = new HashMap<>();
        notification.put("link", "https://play.google.com/store/apps/details?id=com.copula.android");
        notification.put("body", "Checkout copula update");
        notification.put("title", "Update Available");
        notification.put(Util.PAYLOAD_CLICK_ACTION, "updates");

        Map<String, String> data = new HashMap<>();
        data.put("followUserId", "2");
        data.put("followUsername", "heeleeaz");
        data.put("link", "https://play.google.com/store/apps/details?id=com.copula.android");

        fcmMessaging.sendData(pnToken, notification, data);
    }

}
