package com.copulaapp.webservice.models.feed;

import com.copulaapp.webservice.util.JSONInner;
import com.copulaapp.webservice.util.annotations.Column;

/**
 * Created by heeleeaz on 8/3/16.
 */
@JSONInner("result")
public class FeedEntry extends FeedPushEntry {
    public static final int AUDIO = 2, IMAGE = 1, VIDEO = 3;

    @Column(type = String.class, name = "createdAt")
    public String createdAt;

    @Column(type = String.class, name = "updatedAt")
    public String updatedAt;
    @Column(type = Integer.class, name = "likeCount")
    public int likeCount;

    public FeedEntry(String feedId) {
        this.feedId = feedId;
    }

    public FeedEntry() {
    }

}//end
