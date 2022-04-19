package com.copulaapp.webservice.models.profile;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.models.DaoException;
import com.copulaapp.webservice.models.profile.entry.UserAccountEntry;
import com.copulaapp.webservice.util.CopulaDate;
import com.copulaapp.webservice.util.DaoModelMapper;
import com.copulaapp.webservice.util.SQLHelper;

import java.sql.*;
import java.util.List;

/**
 * Created by heeleeaz on 7/2/16.
 */
public class UserAccountDao {
    protected static final String TABLE = "copula.userProfile";
    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    public static int getAccountType(String accountType) {
        switch (accountType) {
            case "native":
                return 0;
            case "facebook":
                return 1;
            default:
                return -1;
        }
    }

    public String newAccount(UserAccountEntry model) throws Exception {
        Connection connection = null;
        try {
            connection = mConnectionPool.getConnection();

            String sql = "INSERT INTO " + TABLE;
            sql += "(username,email,password,accountType,tpUserId,pnToken,createdAt)";
            sql += " VALUES(?,?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, model.username);
            statement.setString(2, model.email);
            statement.setString(3, model.password);
            statement.setInt(4, model.accountType);
            statement.setString(5, model.tpUserId);
            statement.setString(6, model.pnToken);
            statement.setString(7, CopulaDate.getCurrentTime());


            if (statement.executeUpdate() != 0) {
                ResultSet rs = connection.
                        createStatement().executeQuery("SELECT LAST_INSERT_ID()");
                if (rs.next()) return rs.getString(1);
            }
            throw new DaoException("Account creation failed");
        } finally {
            mConnectionPool.closeConnection(connection);
        }
    }

    private UserAccountEntry getAccount(String column, String value) throws Exception {
        PreparedStatement statement = null;

        try {
            String prj = "username,password,email,tpUserId,userId,accountType,createdAt,pnToken";
            String sql = "SELECT " + prj + " FROM " + TABLE + " WHERE " + column + " =?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, value);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return DaoModelMapper.map(result, UserAccountEntry.class);
            } else return null;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public String getPNToken(String userId) throws Exception {
        PreparedStatement statement = null;

        try {
            String sql = "SELECT pnToken  FROM " + TABLE + " WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, userId);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("pnToken");
            } else return null;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean updateUsername(String userId, String username) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "UPDATE " + TABLE + " SET username=? WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, userId);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean updatePNToken(String userId, String pnToken) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "UPDATE " + TABLE + " SET pnToken=? WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, pnToken);
            statement.setString(2, userId);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public boolean updatePassword(String userId, String password) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "UPDATE " + TABLE + " SET password=? WHERE userId=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, password);
            statement.setString(2, userId);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public ResultSet fetchAccountResultSet() throws Exception {
        String sql = "SELECT email,userId,pnToken,username FROM " + TABLE;
        Statement statement = mConnectionPool.getConnection().createStatement();
        return statement.executeQuery(sql);
    }

    public ResultSet fetchAccountResultSet(String column, List<String> in)
            throws SQLException {
        String sql = "SELECT email,userId,pnToken,username FROM " + TABLE;
        sql += " WHERE " + SQLHelper.in("email", in);
        Statement statement = mConnectionPool.getConnection().createStatement();
        return statement.executeQuery(sql);
    }


    public boolean isUsernameExists(String username) {
        try {
            UserAccountEntry e = getAccount("username", username);
            return e != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmailExists(String email) {
        try {
            UserAccountEntry e = getAccount("email", email);
            return e != null;
        } catch (Exception e) {
            return false;
        }
    }


    public UserAccountEntry getAccount(String userId) {
        try {
            return getAccount("userId", userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserAccountEntry getAccountWithEmail(String email) {
        try {
            return getAccount("email", email);
        } catch (Exception e) {
            return null;
        }
    }

    public UserAccountEntry getAccountWithUsername(String username) {
        try {
            return getAccount("username", username);
        } catch (Exception e) {
            return null;
        }
    }

    public UserAccountEntry getThirdPartyAccount(String tpUserId) {
        try {
            return getAccount("tpUserId", tpUserId);
        } catch (Exception e) {
            return null;
        }
    }
}
