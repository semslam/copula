package com.copulaapp.webservice.models.profile.binder;

import com.copulaapp.webservice.util.annotations.Column;

/**
 * Created by heeleaz on 10/3/17.
 */
public class FeedBindableEntry extends PosterBindableEntry {
    public String thumbnailUrl, dataUrl, streamUrl;

    public String feedId;
    @Column(name = "mediaType", type = Integer.class)
    public int mediaType;

}
