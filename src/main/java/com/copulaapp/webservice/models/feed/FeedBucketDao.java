package com.copulaapp.webservice.models.feed;

import com.copulaapp.mediaws.FeedMediaManager;
import com.copulaapp.mediaws.ProfileMediaManager;
import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.models.DaoException;
import com.copulaapp.webservice.models.profile.RecordCountDao;
import com.copulaapp.webservice.util.CopulaDate;
import com.copulaapp.webservice.util.DaoModelMapper;
import com.copulaapp.webservice.util.Pager;
import com.copulaapp.webservice.util.SQLHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.copulaapp.webservice.models.profile.RecordCountDao.FEED_COUNT;

/**
 * Created by heeleeaz on 7/2/16.
 */
public class FeedBucketDao {
    protected static final String TABLE = "copula.feedBucket";
    private static final int TYPE_ID = 10;

    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();
    private LikeRegisterDao likeRegisterDao = new LikeRegisterDao();
    private UserContentDao userContentDao = new UserContentDao();
    private FollowerContentDao followerContentDao = new FollowerContentDao();
    private RecordCountDao recordCountDao = new RecordCountDao();

    public String postFeed(FeedPushEntry push) throws SQLException, DaoException {
        Connection connection = null;
        try {
            connection = mConnectionPool.getConnection();
            String sql = "INSERT INTO " + TABLE
                    + "(posterId,feedType,mediaType,mediaTitle,mediaCaption,"
                    + "mediaMeta,createdAt,tag) VALUES(?,?,?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, push.posterId);
            statement.setInt(2, push.feedType);
            statement.setInt(3, push.mediaType);
            statement.setString(4, push.mediaTitle);
            statement.setString(5, push.mediaCaption);
            statement.setString(6, push.mediaMeta);
            statement.setString(7, CopulaDate.getCurrentTime());
            statement.setString(8, push.tag);

            String feedId = null;
            if (statement.executeUpdate() != 0) {
                String sqlLastId = "SELECT LAST_INSERT_ID()";
                ResultSet rs = connection.createStatement().executeQuery(sqlLastId);
                if (rs.next()) feedId = rs.getString(1);
            } else throw new DaoException("Feed Upload Failed");

            if (userContentDao.putContent(push.posterId, TYPE_ID, feedId)) {
                recordCountDao.plusCount(push.posterId, FEED_COUNT, 1);
                followerContentDao.pushToFollowersAsync(TYPE_ID, feedId, push.posterId);
                return feedId;
            } else return null;
        } finally {
            mConnectionPool.closeConnection(connection);
        }
    }

    List<FeedEntry>
    getFeedList(List<String> feedsId, String peeperId) throws Exception {
        Statement statement = null;
        try {
            if (feedsId == null || feedsId.size() == 0) return null;
            String sql = "SELECT * FROM " + TABLE + " WHERE ";
            sql += SQLHelper.in("feedId", feedsId) + " ORDER BY feedId DESC";

            statement = mConnectionPool.getConnection().createStatement();
            ResultSet result = statement.executeQuery(sql);

            ArrayList<FeedEntry> entries = new ArrayList<>();
            while (result.next()) {
                entries.add(DaoModelMapper.map(result, FeedEntry.class));
            }

            if (entries.size() > 0) return joinUserProfile(peeperId, entries);
            else return null;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public FeedEntry getFeed(String feedId, String peeperId) throws Exception {
        List<String> fl = new ArrayList<>(1);
        fl.add(feedId);

        List<FeedEntry> result = getFeedList(fl, peeperId);
        if (result != null && result.size() > 0) return result.get(0);
        else return null;
    }

    public Pager<FeedEntry> getUserFeeds(String userId, String peeperId, String limit)
            throws Exception {
        if (limit == null || limit.equals("")) limit = "0,20";

        Pager<String> fFeedsId = userContentDao.getContents(userId, TYPE_ID, limit);
        if (fFeedsId == null) return null;//no feed available user this user

        List<FeedEntry> feedList = getFeedList(fFeedsId.packet, peeperId);
        if (feedList != null) {
            return new Pager<FeedEntry>(feedList, fFeedsId.getCurrentPageToken());
        } else return null;
    }

    public boolean delFeed(String feedId, String posterId) throws Exception {
        PreparedStatement statement = null;
        try {
            String sql = "DELETE FROM " + TABLE + " WHERE feedId=? AND posterId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, feedId);
            statement.setString(2, posterId);
            if (statement.executeUpdate() != 0) {
                recordCountDao.minusCount(posterId, FEED_COUNT, 1);
                userContentDao.deleteContent(TYPE_ID, feedId);
                return true;
            } else return false;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean delFeeds(String posterId) throws SQLException {
        PreparedStatement statement = null;
        try {
            String sql = "DELETE FROM " + TABLE + " WHERE posterId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, posterId);

            if (statement.executeUpdate() != 0) {
                if (userContentDao.deleteContents(posterId))
                    recordCountDao.setCount(posterId, FEED_COUNT, 0);
                return true;
            } else return false;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean likeFeed(String feedId, String likerId) throws SQLException {
        if (!likeRegisterDao.likeFeed(feedId, likerId)) return false;

        PreparedStatement statement = null;
        try {
            String sql = "UPDATE " + TABLE + " SET likeCount=likeCount+1 WHERE feedId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, feedId);
            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean unlikeFeed(String feedId, String likerId) throws SQLException {
        if (!likeRegisterDao.unlikeFeed(feedId, likerId)) return false;

        PreparedStatement statement = null;
        try {
            String sql = "UPDATE " + TABLE + " SET likeCount=likeCount-1 WHERE feedId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, feedId);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean isFeedLiked(String feedId, String userId) throws SQLException {
        return likeRegisterDao.isFeedLiked(feedId, userId);
    }

    @Deprecated
    public <T extends FeedEntry> List<T>
    joinUserProfile(String userId, List<T> feedsModel) throws Exception {
        List<String> pList = new ArrayList<>(feedsModel.size());
        for (FeedEntry e : feedsModel) pList.add(e.posterId);

        Map<String, String> accounts = getUsername(pList);
        if (accounts == null || accounts.size() < 1) return feedsModel;
        for (FeedEntry f : feedsModel) {
            f.posterUsername = accounts.get(f.posterId);
            f.posterImageUrl = ProfileMediaManager.getImage(f.posterId);
            f.isFollowing = true;
        }

        List<String> fList = new ArrayList<>(feedsModel.size());
        for (FeedEntry e : feedsModel) {
            if (e.mediaType == FeedEntry.VIDEO) {
                e.thumbnailUrl = FeedMediaManager.getVideoThumbnail(e.feedId);
                e.dataUrl = e.streamUrl = FeedMediaManager.getVideo(e.feedId);
            } else {
                e.dataUrl = e.thumbnailUrl = FeedMediaManager.getImage(e.feedId);
            }

            fList.add(e.feedId);//add so as to precess feed information
        }

        List<String> likedFeeds = likeRegisterDao.getLikedFeeds(userId, fList);
        for (String fid : likedFeeds) {
            int index = fList.indexOf(fid);
            if (index != -1) feedsModel.get(index).isLiked = true;
        }

        return feedsModel;
    }//END

    private Map<String, String> getUsername(List<String> userIdList) throws Exception {
        Statement statement = null;
        try {
            String sql = "SELECT username, userId FROM copula.userProfile WHERE ";
            sql += SQLHelper.in("userId", userIdList);
            statement = mConnectionPool.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            Map<String, String> map = new HashMap<>();
            while (resultSet.next()) {
                String value = resultSet.getString("username");
                map.put(resultSet.getString("userId"), value);
            }
            return map;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }
}
