package com.copulaapp.webservice.models.threads;

import com.copulaapp.webservice.models.profile.binder.PosterBindableEntry;

/**
 * Created by heeleaz on 10/2/17.
 */
public class MediaThreadEntry extends PosterBindableEntry {
    public String posterId, title, artist, album, threadId;
    public String activity;
    public int likeCount, commentCount;
    public String mediaId;
}
