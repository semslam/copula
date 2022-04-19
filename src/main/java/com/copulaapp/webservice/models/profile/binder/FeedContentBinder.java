package com.copulaapp.webservice.models.profile.binder;

import com.copulaapp.mediaws.FeedMediaManager;
import com.copulaapp.webservice.models.feed.FeedEntry;
import com.copulaapp.webservice.models.feed.LikeRegisterDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heeleaz on 10/3/17.
 */
public class FeedContentBinder<T extends FeedBindableEntry> {
    private LikeRegisterDao likeRegisterDao = new LikeRegisterDao();

    private List<String> feedIdList;
    private List<T> feedList;

    public FeedContentBinder(List<T> feedList) {
        this.feedList = feedList;
        this.feedIdList = new ArrayList<>(feedList.size());
        for (T c : feedList) feedIdList.add(c.feedId);
    }

    public void bindFeedContentFiles() {
        for (FeedBindableEntry e : feedList) {
            if (e.mediaType == FeedEntry.VIDEO) {
                e.thumbnailUrl = FeedMediaManager.getVideoThumbnail(e.feedId);
                e.dataUrl = e.streamUrl = FeedMediaManager.getVideo(e.feedId);
            } else {
                e.dataUrl = e.thumbnailUrl = FeedMediaManager.getImage(e.feedId);
            }
        }
    }

    public void bindLikeRegister(String userId) throws SQLException {
        List<String> likedFeeds = likeRegisterDao.getLikedFeeds(userId, feedIdList);
        for (String fid : likedFeeds) {
            int index = feedIdList.indexOf(fid);
            if (index != -1) feedList.get(index).isLiked = true;
        }
    }
}
