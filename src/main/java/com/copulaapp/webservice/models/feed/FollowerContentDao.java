package com.copulaapp.webservice.models.feed;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.models.profile.FollowBoardDao;
import com.copulaapp.webservice.util.Pager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by heeleaz on 8/25/16.
 */
public class FollowerContentDao {
    private static final String TABLE = "copula.followerContent";
    private ExecutorService pushThreadPool = Executors.newCachedThreadPool();
    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    private FollowBoardDao followBoardDao = new FollowBoardDao();

    public void pushToFollowersAsync(int typeId, String contentId, String posterId) {
        pushThreadPool.execute(() -> {
            try (ResultSet r =
                         followBoardDao.getFollowerList(posterId, null)) {
                pushToFollowers(r, typeId, contentId, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void
    pushToFollowers(ResultSet r, int typeId, String contentId, int rf) throws Exception {
        List<String> followingList = new ArrayList<>();

        r.absolute(rf);//start reading from this position: !indexed
        while (followingList.size() <= 200 && r.next()) {
            followingList.add(r.getString("userId"));
        }

        if (followingList.size() > 0)
            this.batchPutContent(typeId, contentId, followingList);

        if (r.next()) {
            int startPosition = r.getRow() - 1;//restore back to prev pos:next
            pushToFollowers(r, typeId, contentId, startPosition);
        }
    }

    private void
    batchPutContent(int typeId, String contentId, List<String> userIds) throws SQLException {
        try (Connection connection = mConnectionPool.getConnection()) {
            connection.setAutoCommit(false);

            String sql = "INSERT INTO " + TABLE + "(typeId,contentId,userId) VALUES(?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            for (String userId : userIds) {
                statement.setInt(1, typeId);
                statement.setString(2, contentId);
                statement.setString(3, userId);
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
        }
    }

    public Pager<String> getContentId(String userId, String limit) throws SQLException {
        PreparedStatement statement = null;
        try {
            String sql = "SELECT contentId FROM " + TABLE + " WHERE userId=?";
            sql += " ORDER BY _id DESC LIMIT " + limit;
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet result = statement.executeQuery();

            List<String> feedIdList = new ArrayList<>();
            while (result.next()) {
                feedIdList.add(result.getString("contentId"));
            }
            return new Pager<String>(feedIdList, limit);
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }
}
