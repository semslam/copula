package com.copulaapp.webservice.models.profile.entry;

import com.copulaapp.webservice.util.annotations.Column;

/**
 * Created by heeleeaz on 8/3/16.
 */
public class UserProfileEntry extends UserAccountEntry {
    public String bio;
    public String imgUrl;

    @Column(type = Boolean.class)
    public boolean isFollowing;

    @Column(type = Integer.class)
    public int followerCount, followingCount, feedPostCount;
}
