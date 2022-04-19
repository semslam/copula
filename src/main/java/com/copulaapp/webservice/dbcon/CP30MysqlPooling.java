package com.copulaapp.webservice.dbcon;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Created by heeleaz on 8/24/16.
 */
public class CP30MysqlPooling {
    private static CP30MysqlPooling sInstance;
    private ComboPooledDataSource pooledDataSource;

    private Logger logger = Logger.getLogger("CP30MysqlPooling");

    public static void
    initialize(String user, String password) throws Exception {
        if (sInstance == null) {
            sInstance = new CP30MysqlPooling();
            sInstance.connect(user, password);
        }
    }

    public static CP30MysqlPooling getInstance() {
        return sInstance;
    }

    private void connect(String u, String p) throws Exception {
        pooledDataSource = new ComboPooledDataSource();
        pooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
        pooledDataSource.setJdbcUrl("jdbc:mysql://localhost/?useSSL=false");
        pooledDataSource.setUser(u);
        pooledDataSource.setPassword(p);

        pooledDataSource.setMinPoolSize(3);
        pooledDataSource.setAcquireIncrement(5);
        pooledDataSource.setMaxPoolSize(20);
        pooledDataSource.setMaxStatements(201);

        logger.info("MysqlDB:Pool connection successful: -u " + u);
    }

    public Connection getConnection() throws SQLException {
        return pooledDataSource.getConnection();
    }

    public void closeConnection(Connection connection) {
        if (connection == null) return;

        try {
            connection.close();
        } catch (SQLException e) {
        }
    }//END

    public void closeConnection(Statement statement) {
        if (statement == null) return;

        try {
            closeConnection(statement.getConnection());
            statement.close();
        } catch (SQLException e) {
        }
    }//END
}
