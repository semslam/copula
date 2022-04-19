package com.copulaapp.webservice.models.admin;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.models.DaoException;
import com.copulaapp.webservice.util.DaoModelMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by heeleaz on 11/4/16.
 */
public class AdminAccountDao {
    private static final String TABLE = "copula.adminAccount";
    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();


    public boolean newAccount(String username, String password) throws Exception {
        PreparedStatement statement = null;

        try {
            if (isAccountExists(username))
                throw new DaoException("Account Already Exists");

            String sql = "INSERT INTO " + TABLE + "(username,password) VALUES(?,?)";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public AdminAccountEntry getAccount(String username) throws Exception {
        PreparedStatement statement = null;

        try {
            String sql = "SELECT * FROM " + TABLE + " WHERE username=? LIMIT 1";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, username);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return DaoModelMapper.map(result, AdminAccountEntry.class);
            } else return null;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    private boolean isAccountExists(String username) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "SELECT COUNT(1) FROM " + TABLE + " WHERE username=? LIMIT 1";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, username);

            ResultSet result = statement.executeQuery();
            return result.next() && result.getInt(1) == 1;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }
}
