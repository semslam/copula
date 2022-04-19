package com.copulaapp.webservice.models.profile;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.mysql.jdbc.StringUtils;
import org.json.JSONArray;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by heeleaz on 9/22/17.
 */
public class UserInterestDao extends UserProfileDao {
    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    public boolean newInterest(String userId, String interest) throws Exception {
        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO " + TABLE + "(interest) VALUES(?) WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, interest);
            statement.setString(2, userId);
            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean addInterest(String userId, String interest) throws Exception {
        JSONArray interestDB = new JSONArray(getUserInterest(userId));
        JSONArray newInterest = new JSONArray(interest);

        for (int i = 0; i < newInterest.length(); i++) {
            String aValue = newInterest.getString(i);
            boolean exists = false;
            for (int j = 0; j < interestDB.length(); j++) {
                if (exists = aValue.equals(interestDB.getString(j))) break;
            }
            if (!exists) interestDB.put(aValue);
        }
        return newInterest(userId, interestDB.toString());
    }

    public String getUserInterest(String userId) throws Exception {
        PreparedStatement statement = null;
        try {
            String sql = "SELECT interest FROM " + TABLE + " WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);

            ResultSet result = statement.executeQuery();
            String interest = null;
            if (result.next()) interest = result.getString("interest");
            return (!StringUtils.isNullOrEmpty(interest))
                    ? interest : new JSONArray().toString();
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public ResultSet getUsersWithInterest(String interest) throws SQLException {
        PreparedStatement statement = null;
        String sql = "SELECT * FROM " + TABLE + " WHERE " +
                "JSON_CONTAINS(?,interest) OR JSON_CONTAINS(interest,?)";
        statement = mConnectionPool.getConnection().prepareStatement(sql);
        statement.setString(1, interest);
        statement.setString(2, interest);
        return statement.executeQuery();
    }
}
