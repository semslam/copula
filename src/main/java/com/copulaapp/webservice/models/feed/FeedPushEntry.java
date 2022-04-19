package com.copulaapp.webservice.models.feed;

import com.copulaapp.webservice.models.profile.binder.FeedBindableEntry;
import com.copulaapp.webservice.util.annotations.Column;

public class FeedPushEntry extends FeedBindableEntry {
    private static final String DEFAULT_TAG = "[\"comedy\"]";
    public static int EXPLORE_FEED = 1;
    public static int USER_FEED = 2;
    public static int SPONSORED_POST = 3;

    public String mediaTitle, mediaCaption, mediaMeta;
    public String tag = DEFAULT_TAG;

    @Column(name = "feedType", type = Integer.class)
    public int feedType;
}