package com.copulaapp.webservice.models.feed.notification;

import com.copulaapp.messaging.cloudmessage.http.FCMMessaging;
import com.copulaapp.messaging.cloudmessage.xmpp.Util;
import com.copulaapp.webservice.models.feed.FeedEntry;
import com.copulaapp.webservice.models.profile.entry.UserProfileEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by heeleaz on 7/6/17.
 */
public class FeedReactAnnouncer {
    private static final String ACTION_FEED_LIKE = "feedLike";

    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private static FCMMessaging fcmMessaging = FCMMessaging.getInstance();

    public static void
    pushFeedLikeAsync(String pnToken, UserProfileEntry liker, FeedEntry e) {
        threadPool.execute(() -> {
            Map<String, String> data = new HashMap<>();
            data.put("feedId", e.feedId);
            data.put("mediaType", "" + e.mediaType);
            data.put("mediaTitle", e.mediaTitle);
            data.put("likerUsername", liker.username);
            data.put("likerUserId", liker.userId);

            Map<String, String> notification = new HashMap<>();
            notification.put(Util.PAYLOAD_TITLE, "New Post Like");
            notification.put(Util.PAYLOAD_BODY, liker.username + " liked your post");
            notification.put(Util.PAYLOAD_CLICK_ACTION, ACTION_FEED_LIKE);

            fcmMessaging.sendData(pnToken, notification, data);
        });
    }
}
