package com.copulaapp.webservice.models.profile.notification;

import com.copulaapp.config.ConfigModel;
import com.copulaapp.messaging.mailer.BatchJob;
import com.copulaapp.messaging.mailer.DefaultConfig;
import com.copulaapp.messaging.mailer.HtmlMailSender;
import com.copulaapp.messaging.mailer.MailDispatcher;

/**
 * Created by heeleaz on 9/9/17.
 */
public class FriendSuggestionMail implements BatchJob {
    private static String readHtml;
    private String fullname;
    private String receiverUsername, receiverUserId, recipient;
    private String platform;
    private HtmlMailSender mHtmlMailSender;

    private String editingHtmlString;

    FriendSuggestionMail() throws Exception {
        String htmlFile = "/copula/mail/htmlfile/friend_suggestion_mail.html";

        mHtmlMailSender = new HtmlMailSender(new DefaultConfig());

        ConfigModel mConfigDao = new ConfigModel();
        String htmlFileKey = "updFriendSuggestionHtmlFile";
        if (readHtml == null ||
                Boolean.valueOf(mConfigDao.getConfig(htmlFileKey))) {
            readHtml = HtmlMailSender.readHtmlFile(htmlFile);
            mConfigDao.updateConfig(htmlFileKey, "false");
        }

        editingHtmlString = readHtml;

        mHtmlMailSender.setFrom("Copula <no-reply@copulaapp.com>");
    }

    void setUserImageUrl(String imageUrl) {
        editingHtmlString = editingHtmlString.replace("$userImageUrl", imageUrl);
    }

    void setUsername(String username) {
        editingHtmlString = editingHtmlString.replace("$username", username);
    }

    void setFullname(String fullname) {
        this.fullname = fullname;
        editingHtmlString = editingHtmlString.replace("$fullname", fullname);
    }

    void setReceiverUsername(String username) {
        this.receiverUsername = username;
    }

    void setReceiverUserId(String userId) {
        this.receiverUserId = userId;
    }

    void setReceiverEmail(String email) {
        this.recipient = email;
    }

    void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public boolean sendMessage() {
        String edited = editingHtmlString
                .replace("$receiverUsername", receiverUsername)
                .replace("$receiverUserId", receiverUserId);

        try {
            mHtmlMailSender.setMessage(edited);
            mHtmlMailSender.setSubject(composeSubject(receiverUsername));
            mHtmlMailSender.setRecipient(recipient);
            mHtmlMailSender.sendMessage();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String composeSubject(String rUsername) {
        return String.format("%s, Follow your %s friend %s", rUsername, platform, fullname);
    }

    void sendMessageInBatch() {
        MailDispatcher dispatcher = MailDispatcher.getInstance();
        dispatcher.postJob(this);
    }
}
