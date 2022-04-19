package com.copulaapp.webservice.models.profile;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by heeleeaz on 8/11/16.
 */
public class RecordCountDao {
    public static final String FEED_COUNT = "feedPostCount";
    public static final String FOLLOWER_COUNT = "followerCount";
    public static final String FOLLOWING_COUNT = "followingCount";
    private static final String TABLE = "copula.userProfile";

    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    public boolean plusCount(String userId, String target, int plus) throws SQLException {
        PreparedStatement statement = null;
        try {
            String sql = "UPDATE " + TABLE + " SET ";
            sql += target + "=" + target + "+" + plus + " WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean minusCount(String userId, String target, int minus) throws SQLException {
        PreparedStatement statement = null;
        try {
            String sql = "UPDATE " + TABLE;
            sql += " SET " + target + "=" + target + "-" + minus + " WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean setCount(String userId, String target, int count) throws SQLException {
        PreparedStatement statement = null;
        try {
            String sql = "UPDATE " + TABLE + " SET " + target + "=? WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setInt(1, count);
            statement.setString(2, userId);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }


    public int getCount(String userId, String target) throws SQLException {
        PreparedStatement statement = null;
        try {
            String sql = "SELECT " + target + " FROM " + TABLE + " WHERE userId=? LIMIT 1";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet result = statement.executeQuery();

            if (result.next()) return result.getInt(target);
            else return -1;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }
}
