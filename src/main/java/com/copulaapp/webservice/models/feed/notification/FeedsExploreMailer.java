package com.copulaapp.webservice.models.feed.notification;

import com.copulaapp.config.ConfigModel;
import com.copulaapp.messaging.mailer.BatchJob;
import com.copulaapp.messaging.mailer.DefaultConfig;
import com.copulaapp.messaging.mailer.HtmlMailSender;
import com.copulaapp.messaging.mailer.MailDispatcher;

import javax.mail.MessagingException;

/**
 * Created by heeleaz on 9/9/17.
 */
public class FeedsExploreMailer implements BatchJob {
    private static String readHtml;
    private String userId, recipient;
    private HtmlMailSender mHtmlMailSender;

    private String editingHtmlString;

    FeedsExploreMailer() throws Exception {
        String htmlFile = "/copula/mail/htmlfile/explore_html_mail.html";
        mHtmlMailSender = new HtmlMailSender(new DefaultConfig());

        ConfigModel mConfigDao = new ConfigModel();
        String htmlFileKey = "updExploreHtmlFile";
        if (readHtml == null ||
                Boolean.valueOf(mConfigDao.getConfig(htmlFileKey))) {
            readHtml = HtmlMailSender.readHtmlFile(htmlFile);
            mConfigDao.updateConfig(htmlFileKey, "false");
        }

        editingHtmlString = readHtml;

        mHtmlMailSender.setFrom("Copula <no-reply@copulaapp.com>");
    }

    void setCaption(String caption) {
        if (caption == null) return;
        editingHtmlString = editingHtmlString.replace("$feedCaption", caption);
    }

    void setExploreImageUrl(String exploreImageUrl) {
        if (exploreImageUrl == null) return;
        editingHtmlString = editingHtmlString
                .replace("$exploreImageUrl", exploreImageUrl);
    }

    void setFeedTitle(String feedTitle, String posterUsername) throws MessagingException {
        String title = (feedTitle == null)
                ? posterUsername : feedTitle + " - " + posterUsername;
        editingHtmlString = editingHtmlString.replace("$feedTitle", title);

        String subject = String.format("Explore new post shared by %s and others",
                posterUsername);
        mHtmlMailSender.setSubject(subject);
    }

    void setReceiverEmail(String recipient) {
        this.recipient = recipient;
    }

    void setReceiverUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean sendMessage() {
        try {
            String message = editingHtmlString.replace("$userId", userId);
            mHtmlMailSender.setMessage(message);
            mHtmlMailSender.setRecipient(recipient);
            mHtmlMailSender.sendMessage();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    void sendMessageInBatch() {
        MailDispatcher dispatcher = MailDispatcher.getInstance();
        dispatcher.postJob(this);
    }
}
