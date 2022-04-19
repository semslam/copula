package com.copulaapp.webservice.models.support;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.util.CopulaDate;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by heeleaz on 12/11/16.
 */
public class UserFeedbackDao {
    private static final String TABLE = "copula.userFeedback";
    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    public boolean deviceFeatureFeedback(String email, String deviceId, String feature
            , String message) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "INSERT INTO " + TABLE;
            sql += "(email,deviceId,feature,message,createdAt) VALUES(?,?,?,?,?)";

            statement = mConnectionPool.getConnection().prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, deviceId);
            statement.setString(3, feature);
            statement.setString(4, message);
            statement.setString(5, CopulaDate.getCurrentTime());

            return statement.executeUpdate() != 0;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }//END
}
