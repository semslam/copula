package com.copulaapp.webservice.models.admin;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.models.DaoException;
import com.copulaapp.webservice.util.DaoModelMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by heeleaz on 11/4/16.
 */
public class APIAuthTokenDao {
    private static final String TABLE = "copula.apiAuthToken";
    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    public APIAuthTokenEntry newToken(String user, String token, long expiresIn)
            throws SQLException, DaoException {
        PreparedStatement statement = null;

        try {
            //TODO ttl required for exipred time
            String sql = "INSERT INTO " + TABLE + "(username,apiToken,expiresOn)";
            sql += "VALUES(?,?,?)";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, user);
            statement.setString(2, token);
            statement.setDate(3, new Date(expiresIn));

            if (statement.executeUpdate() != 0) {
                APIAuthTokenEntry t = new APIAuthTokenEntry();
                t.apiToken = token;
                t.username = user;
                t.expiresOn = new Date(System.currentTimeMillis() + expiresIn);
                return t;
            } else throw new DaoException("Token could not be generated");
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }

    public APIAuthTokenEntry getToken(String username) throws Exception {
        PreparedStatement statement = null;

        try {
            String sql = "SELECT * FROM " + TABLE + " WHERE username=? LIMIT 1";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, username);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return DaoModelMapper.map(result, APIAuthTokenEntry.class);
            } else return null;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }
}
