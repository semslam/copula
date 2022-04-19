package com.copulaapp.webservice.models.feed;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.util.CopulaDate;
import com.copulaapp.webservice.util.SQLHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heeleeaz on 7/7/16.
 */
public class LikeRegisterDao {
    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    private String TABLE = "copula.feedLikeRegister";

    boolean likeFeed(String feedId, String userId) throws SQLException {
        if (isFeedLiked(feedId, userId)) return false;//feed already liked

        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO " + TABLE + " (feedId, userId, createdAt)";
            sql += " VALUES (?,?,?)";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, feedId);
            statement.setString(2, userId);
            statement.setString(3, CopulaDate.getCurrentTime());

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    boolean unlikeFeed(String feedId, String userId) throws SQLException {
        if (!isFeedLiked(feedId, userId)) return false;//no feed to unlike
        PreparedStatement statement = null;

        try {
            String sql = "DELETE FROM " + TABLE + " WHERE feedId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, feedId);
            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    boolean isFeedLiked(String feedId, String userId) throws SQLException {
        PreparedStatement statement = null;
        try {
            String sql = "SELECT COUNT(1) FROM " + TABLE + " WHERE userId=? AND feedId=?";
            sql += " LIMIT 1";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, feedId);
            ResultSet result = statement.executeQuery();

            return result.next() && result.getInt(1) != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public List<String>
    getLikedFeeds(String userId, List<String> inFeeds) throws SQLException {
        PreparedStatement statement = null;
        try {
            String sql = "SELECT feedId FROM " + TABLE + " WHERE userId=?";
            sql += " AND " + SQLHelper.in("feedId", inFeeds);
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();

            List<String> l = new ArrayList<>();
            while (resultSet.next()) l.add(resultSet.getString("feedId"));
            return l;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }
}
