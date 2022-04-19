package com.copulaapp.webservice.models.profile;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.models.profile.entry.UserProfileEntry;
import com.copulaapp.webservice.util.CopulaDate;
import com.copulaapp.webservice.util.Pager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.copulaapp.webservice.models.profile.RecordCountDao.FOLLOWER_COUNT;
import static com.copulaapp.webservice.models.profile.RecordCountDao.FOLLOWING_COUNT;

/**
 * Created by heeleeaz on 7/6/16.
 */
public class FollowBoardDao {
    private static final String TABLE = "copula.userFollow";
    private static final int RESULTS_PER_PAGE = 30;
    private RecordCountDao mRecordCounter = new RecordCountDao();
    private UserProfileDao userProfileDAO = new UserProfileDao();

    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    public ResultSet getFollowerList(String userId, String limit) throws SQLException {
        Connection connection = mConnectionPool.getConnection();
        String sql = "SELECT userId FROM " + TABLE + " WHERE followUserId=?";
        if (limit != null) sql += " LIMIT " + limit;

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, userId);
        return statement.executeQuery();
    }

    public boolean follow(String userId, String followUserId) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "INSERT INTO " + TABLE;
            sql += "(userId, followUserId, createdAt) VALUES(?,?,?)";

            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, followUserId);
            statement.setString(3, CopulaDate.getCurrentTime());

            if (statement.executeUpdate() != 0) {
                mRecordCounter.plusCount(userId, FOLLOWING_COUNT, 1);
                mRecordCounter.plusCount(followUserId, FOLLOWER_COUNT, 1);
                return true;
            } else return false;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean unfollow(String userId, String followUserId) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "DELETE FROM " + TABLE + " WHERE userId=? AND followUserId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, followUserId);

            if (statement.executeUpdate() != 0) {
                mRecordCounter.minusCount(userId, FOLLOWING_COUNT, 1);
                mRecordCounter.minusCount(followUserId, FOLLOWER_COUNT, 1);
                return true;
            } else return false;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public String getPID(String userId, String followUserId) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "SELECT _id FROM " + TABLE + " WHERE userId=? AND followUserId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, followUserId);

            ResultSet result = statement.executeQuery();
            if (result.first()) return result.getString("_id");
            return null;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean isFollowing(String userId, String followUserId) throws SQLException {
        return getPID(userId, followUserId) != null;
    }

    public Pager<UserProfileEntry> getFollowings(String userId, String limit) throws Exception {
        if (limit == null) limit = "0," + RESULTS_PER_PAGE;

        List<String> userIds = getFollowingList(userId, limit);
        List<UserProfileEntry> l = userProfileDAO.getProfiles(userIds);
        for (UserProfileEntry u : l) u.isFollowing = true;
        return new Pager<>(l, limit);
    }

    public Pager<UserProfileEntry> getFollowers(String userId, String limit) throws Exception {
        if (limit == null) limit = "0," + RESULTS_PER_PAGE;

        try (ResultSet result = getFollowerList(userId, limit)) {
            ArrayList<String> f_ids = new ArrayList<>();
            while (result.next()) {
                f_ids.add(result.getString("userId"));
            }

            if (f_ids.size() > 0) {
                return new Pager<>(userProfileDAO.getProfiles(f_ids), limit);
            } else return Pager.NULL;
        }
    }

    private List<String> getFollowingList(String userId, String limit) throws Exception {
        PreparedStatement statement = null;
        try {
            String sql = "SELECT followUserId FROM " + TABLE + " WHERE userId=? LIMIT " + limit;
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet result = statement.executeQuery();

            ArrayList<String> userIds = new ArrayList<>();
            while (result.next()) {
                userIds.add(result.getString("followUserId"));
            }
            return userIds;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }
}
