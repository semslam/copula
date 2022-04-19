package com.copulaapp.webservice.controllers.profile;

import com.copulaapp.messaging.cloudmessage.FCM;
import com.copulaapp.messaging.cloudmessage.http.FCMAdmin;
import com.copulaapp.messaging.cloudmessage.http.FCMMessaging;
import com.copulaapp.messaging.cloudmessage.xmpp.Util;
import com.copulaapp.webservice.models.feed.notification.ExploreAnnouncer;
import com.copulaapp.webservice.models.profile.entry.UserProfileEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by heeleaz on 7/6/17.
 */
public class ProfileCloudMSGAware {
    private static final String UPDATES = "updatesWithLink";
    private static final String FOLLOW_SUGGESTION = "followSuggestion";
    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private static FCMMessaging fcmMessaging = FCMMessaging.getInstance();

    static void subscribeAllAsync(String pnToken) {
        threadPool.execute(() -> {
            FCMAdmin messaging = new FCMAdmin(FCM.SERVER_KEY);
            messaging.subscribeToTopic(UPDATES, pnToken);
            messaging.subscribeToTopic(FOLLOW_SUGGESTION, pnToken);
            messaging.subscribeToTopic(ExploreAnnouncer.TOPIC_EXPLORE_FEED, pnToken);
        });
    }

    static void pushFollowMessageAsync(String userId, UserProfileEntry fp) {
        threadPool.execute(() -> {
            Map<String, String> data = new HashMap<>();
            data.put("userId", userId);
            data.put("followUserId", fp.userId);
            data.put("followUsername", fp.username);
            data.put("followImgUrl", fp.imgUrl);

            Map<String, String> notification = new HashMap<>();
            notification.put(Util.PAYLOAD_TITLE, "1 New Follow");
            notification.put(Util.PAYLOAD_BODY,
                    fp.username + " started following you");
            notification.put(Util.PAYLOAD_CLICK_ACTION, "newFollow");

            fcmMessaging.sendData(fp.pnToken, notification, data);
        });
    }
}