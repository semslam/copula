package com.copulaapp.webservice.models.feed;

import com.copulaapp.webservice.util.Pager;

import java.util.List;

/**
 * Created by heeleaz on 9/22/17.
 */
public class TimelineFeedDao extends FeedBucketDao {
    private FollowerContentDao followerContentDao = new FollowerContentDao();

    public Pager<FeedEntry> getTimeLineFeeds(String userId, String limit) throws Exception {
        if (limit == null || limit.equals("")) limit = "0,20";

        Pager<String> fFeedsId = followerContentDao.getContentId(userId, limit);
        if (fFeedsId == null) return null;//no timeline available for this user

        List<FeedEntry> feedList = getFeedList(fFeedsId.packet, userId);
        if (feedList != null) {
            return new Pager<FeedEntry>(feedList, fFeedsId.getCurrentPageToken());
        } else return null;
    }

}
