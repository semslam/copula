package com.copulaapp.webservice.models.profile.binder;

import com.copulaapp.webservice.util.annotations.Column;

/**
 * Created by heeleaz on 10/3/17.
 */
public class PosterBindableEntry {
    public String posterUsername, posterImageUrl, posterId;

    @Column(type = Boolean.class, name = "isFollowing")
    public boolean isFollowing;
    @Column(name = "mediaType", type = Integer.class)
    public int mediaType;
    @Column(type = Boolean.class, name = "isLiked")
    public boolean isLiked;
}
