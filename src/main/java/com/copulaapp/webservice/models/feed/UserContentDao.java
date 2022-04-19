package com.copulaapp.webservice.models.feed;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.util.Pager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heeleaz on 8/24/16.
 */
public class UserContentDao {
    private static final String TABLE = "copula.userContent";
    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    public boolean
    putContent(String userId, int typeId, String contentId) throws SQLException {
        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO " + TABLE + "(userId,typeId,contentId)";
            sql += "VALUES(?,?,?)";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);
            statement.setInt(2, typeId);
            statement.setString(3, contentId);
            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public Pager<String>
    getContents(String userId, int typeId, String limit) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "SELECT * FROM " + TABLE;
            sql += " WHERE userId=? AND typeId=? ORDER BY createdAt DESC LIMIT " + limit;
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);
            statement.setInt(1, typeId);
            ResultSet result = statement.executeQuery();

            List<String> l = new ArrayList<>();
            while (result.next()) l.add(result.getString("feedId"));

            return new Pager<String>(l, limit);
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean deleteContent(int typeId, String contentId) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "DELETE FROM " + TABLE + " WHERE typeId=? AND contentId=? LIMIT 1";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setInt(1, typeId);
            statement.setString(1, contentId);
            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean deleteContents(String userId) throws SQLException {
        PreparedStatement statement = null;
        try {
            String sql = "DELETE FROM " + TABLE + " WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }
}
