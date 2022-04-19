package com.copulaapp.config;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by heeleaz on 9/9/17.
 */
public class ConfigModel {
    protected static final String TABLE = "copula.configuration";
    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    public boolean newConfig(String key, String value, String type) throws Exception {
        Connection connection = null;
        try {
            connection = mConnectionPool.getConnection();

            String sql = "INSERT INTO " + TABLE;
            sql += "(configKey,configValue,configType) VALUES(?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, key);
            statement.setString(2, value);
            statement.setString(3, type);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(connection);
        }
    }

    public boolean updateConfig(String key, String value) throws Exception {
        Connection connection = null;
        try {
            connection = mConnectionPool.getConnection();
            String sql = "UPDATE " + TABLE + " SET configValue=? WHERE configKey=?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, value);
            statement.setString(2, key);

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(connection);
        }
    }

    public String getConfig(String configKey) throws Exception {
        PreparedStatement statement = null;
        try {
            String sql = "SELECT configValue FROM " + TABLE + " WHERE configKey=?";
            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, configKey);
            ResultSet result = statement.executeQuery();

            if (result.next()) return result.getString("configValue");
            return null;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }
}
