package com.copulaapp.webservice.models.profile;

import com.copulaapp.mediaws.ProfileMediaManager;
import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.models.profile.entry.UserProfileEntry;
import com.copulaapp.webservice.util.CopulaDate;
import com.copulaapp.webservice.util.DaoModelMapper;
import com.copulaapp.webservice.util.Pager;
import com.copulaapp.webservice.util.SQLHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heeleeaz on 7/5/16.
 */
public class UserProfileDao extends UserAccountDao {
    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    public boolean newProfile(String userId, String bio) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "UPDATE " + TABLE + " SET bio=?, updatedAt=? WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);

            statement.setString(1, bio);
            statement.setString(2, CopulaDate.getCurrentTime());
            statement.setString(3, userId);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public UserProfileEntry getProfile(String userId) throws Exception {
        PreparedStatement statement = null;

        try {
            String sql = "SELECT * FROM " + TABLE + " WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                UserProfileEntry a = DaoModelMapper.map(result, UserProfileEntry.class);
                a.imgUrl = ProfileMediaManager.getImage(userId);
                return a;
            } else return null;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public Pager<UserProfileEntry> searchProfile(String q, String limit) throws Exception {
        PreparedStatement statement = null;

        try {
            limit = (limit == null) ? "0,20" : limit;
            String sql = "SELECT * FROM " + TABLE + " WHERE username LIKE ? LIMIT " + limit;
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, "%" + q + "%");
            ResultSet result = statement.executeQuery();

            List<UserProfileEntry> feedList = new ArrayList<>();
            while (result.next()) {
                UserProfileEntry a = DaoModelMapper.map(result, UserProfileEntry.class);
                a.imgUrl = ProfileMediaManager.getImage(a.userId);
                feedList.add(a);
            }

            return new Pager<UserProfileEntry>(feedList, limit);
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean updateProfile(String userId, String bio) throws SQLException {
        return newProfile(userId, bio);
    }

    public List<UserProfileEntry> getProfiles(List<String> userIdList) throws Exception {
        Statement statement = null;

        try {
            String sql = "SELECT * FROM " + TABLE + " WHERE ";
            sql += SQLHelper.in("userId", userIdList);

            statement = mConnectionPool.getConnection().createStatement();
            ResultSet result = statement.executeQuery(sql);

            ArrayList<UserProfileEntry> entries = new ArrayList<>();
            while (result.next()) {
                UserProfileEntry e = DaoModelMapper.map(result, UserProfileEntry.class);
                e.imgUrl = ProfileMediaManager.getImage(e.userId);
                entries.add(e);
            }
            return entries;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }
}
