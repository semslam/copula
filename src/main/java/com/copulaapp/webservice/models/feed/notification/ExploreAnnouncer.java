package com.copulaapp.webservice.models.feed.notification;

import com.copulaapp.messaging.cloudmessage.http.FCMMessaging;
import com.copulaapp.messaging.cloudmessage.xmpp.Util;
import com.copulaapp.webservice.models.feed.FeedEntry;
import com.copulaapp.webservice.models.profile.UserAccountDao;
import com.copulaapp.webservice.models.profile.UserInterestDao;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by heeleaz on 9/10/17.
 */
public class ExploreAnnouncer {
    public static final String TOPIC_EXPLORE_FEED = "exploreFeed";
    private static final Logger logger = Logger.getLogger("ExploreAnnouncer");

    private static final String NOTIFICATION_ACTION = "newExplore";
    private static FCMMessaging fcmMessaging = FCMMessaging.getInstance();
    private UserInterestDao userInterestDao = new UserInterestDao();

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private void broadcastExploreNotification(FeedEntry feedEntry) {
        String to = "/topics/" + TOPIC_EXPLORE_FEED;
        sendPushNotification(to, feedEntry);
    }

    private void sendPushNotification(String to, FeedEntry feedEntry) {
        Map<String, String> data = new HashMap<>();
        data.put("posterId", feedEntry.posterId);
        data.put("posterUsername", feedEntry.posterUsername);
        data.put("feedId", feedEntry.feedId);
        data.put("posterImageUrl", feedEntry.posterImageUrl);
        data.put("mediaType", "" + feedEntry.mediaType);
        data.put("mediaTitle", feedEntry.mediaTitle);
        data.put("thumbnailUrl", feedEntry.thumbnailUrl);

        Map<String, String> notification = new HashMap<>();
        notification.put(Util.PAYLOAD_CLICK_ACTION, NOTIFICATION_ACTION);
        notification.put(Util.PAYLOAD_LINK, "http://www.copulaapp.com");

        notification.put(Util.PAYLOAD_TITLE, "Copula");
        String message = feedEntry.posterUsername + " shared a new "
                + ((feedEntry.mediaType == FeedEntry.IMAGE) ? "image" : "video");
        notification.put(Util.PAYLOAD_BODY, message);

        fcmMessaging.sendData(to, notification, data);
    }

    public void sendToAllUsers(FeedEntry feed) throws Exception {
        broadcastExploreNotification(feed);
        UserAccountDao accountDao = new UserAccountDao();
        try (ResultSet result = accountDao.fetchAccountResultSet()) {
            FeedsExploreMailer mailer = new FeedsExploreMailer();
            mailer.setCaption(feed.mediaCaption);
            mailer.setExploreImageUrl(feed.thumbnailUrl);
            mailer.setFeedTitle(feed.mediaTitle, feed.posterUsername);
            while (result.next()) {
                mailer.setReceiverUserId(result.getString("userId"));
                mailer.setReceiverEmail(result.getString("email"));
                mailer.sendMessageInBatch();
            }
        }
    }

    public void sendToAllInterestUsers(FeedEntry feed) throws Exception {
        FeedsExploreMailer mailer = new FeedsExploreMailer();
        mailer.setCaption(feed.mediaCaption);
        mailer.setExploreImageUrl(feed.thumbnailUrl);
        mailer.setFeedTitle(feed.mediaTitle, feed.posterUsername);

        try (ResultSet result = userInterestDao.getUsersWithInterest(feed.tag)) {
            while (result.next()) {
                mailer.setReceiverUserId(result.getString("userId"));
                mailer.setReceiverEmail(result.getString("email"));
                mailer.sendMessageInBatch();

                sendPushNotification(result.getString("pnToken"), feed);
            }
        }
    }

    public void sendToAllInterestUsersAsync(final FeedEntry feedEntry) {
        threadPool.execute(() -> {
            try {
                sendToAllInterestUsers(feedEntry);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "NOTF:Failed: " + e.getMessage());
            }
        });
    }

    public void sendToAllUsersAsync(final FeedEntry feedEntry) {
        threadPool.execute(() -> {
            try {
                sendToAllUsers(feedEntry);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "NOTF:Failed: " + e.getMessage());
            }
        });
    }
}
