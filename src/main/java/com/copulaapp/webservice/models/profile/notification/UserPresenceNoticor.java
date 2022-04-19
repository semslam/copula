package com.copulaapp.webservice.models.profile.notification;

import com.copulaapp.mediaws.ProfileMediaManager;
import com.copulaapp.messaging.cloudmessage.http.FCMMessaging;
import com.copulaapp.messaging.cloudmessage.xmpp.Util;
import com.copulaapp.webservice.models.profile.UserAccountDao;
import com.copulaapp.webservice.models.profile.entry.UserAccountEntry;
import org.json.JSONArray;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by heeleaz on 9/21/17.
 */
public class UserPresenceNoticor {
    private static final Logger logger = Logger.getLogger("UserPresenceNoticor");
    private static final String NOTIFICATION_ACTION = "aFriendJoined";
    private static FCMMessaging fcmMessaging = FCMMessaging.getInstance();

    private String userEmail, userFullname, platform;
    private JSONArray friendsEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public void setFriendsEmail(JSONArray friendsEmail) {
        this.friendsEmail = friendsEmail;
    }

    public void setUserFullname(String fullname) {
        this.userFullname = fullname;
    }

    public void setFriendsEmail(String friendsEmail) throws Exception {
        setFriendsEmail(new JSONArray(friendsEmail));
    }

    public void setSourcePlatform(String accountName3P) {
        this.platform = accountName3P;
    }

    private void pushPresenceNotificationSync() throws Exception {
        UserAccountDao accountDao = new UserAccountDao();

        List<String> list = new ArrayList<>(friendsEmail.length());
        for (int i = 0; i < friendsEmail.length(); i++) {
            list.add(friendsEmail.getString(i));
        }
        ResultSet r = accountDao.fetchAccountResultSet("email", list);

        UserAccountEntry user = accountDao.getAccountWithEmail(userEmail);
        SendMailNotification mail =
                new SendMailNotification(userFullname, user, platform);
        while (r.next()) {
            sendPushNotification(user, r.getString("pnToken"));
            mail.publishWithReceiver(r.getString("email"),
                    r.getString("userId"), r.getString("username"));
        }
    }

    public void pushPresenceNotificationAsync() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                pushPresenceNotificationSync();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Issue sending: " + e.getMessage());
            }
        });
    }

    private void sendPushNotification(UserAccountEntry u, String toPNToken) {
        Map<String, String> data = new HashMap<>();
        data.put("username", u.username);
        data.put("email", u.email);
        data.put("userId", u.userId);

        Map<String, String> notification = new HashMap<>();
        notification.put(Util.PAYLOAD_TITLE, "Follow " + userFullname);

        notification.put(Util.PAYLOAD_BODY, String.format("Follow your %s " +
                "friend %s on Copula", platform, userFullname));
        notification.put(Util.PAYLOAD_CLICK_ACTION, NOTIFICATION_ACTION);
        notification.put(Util.PAYLOAD_LINK, "http://www.copulaapp.com");

        fcmMessaging.sendData(toPNToken, notification, data);
    }

    private class SendMailNotification {
        private FriendSuggestionMail mailer = new FriendSuggestionMail();

        SendMailNotification(String fullname, UserAccountEntry user, String platform)
                throws Exception {
            mailer.setFullname(fullname);
            mailer.setUsername(user.username);
            mailer.setPlatform(platform);
            mailer.setUserImageUrl(ProfileMediaManager.getImage(user.userId));
        }

        void publishWithReceiver(String email, String userId, String username) {
            mailer.setReceiverEmail(email);
            mailer.setReceiverUserId(userId);
            mailer.setReceiverUsername(username);

            mailer.sendMessageInBatch();
        }
    }//END
}
