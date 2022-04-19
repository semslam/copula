package com.copulaapp.webservice.models.profile.binder;

import com.copulaapp.mediaws.ProfileMediaManager;
import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.util.SQLHelper;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by heeleaz on 10/3/17.
 */
public class PosterContentBinder {
    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();

    public <T extends PosterBindableEntry>
    boolean bindPosterProfile(List<T> bindableEntries) throws Exception {
        List<String> pList = new ArrayList<>(bindableEntries.size());
        for (T e : bindableEntries) pList.add(e.posterId);

        Map<String, String> accounts = getUsername(pList);
        if (accounts == null || accounts.size() < 1) return false;

        for (PosterBindableEntry f : bindableEntries) {
            f.posterUsername = accounts.get(f.posterId);
            f.posterImageUrl = ProfileMediaManager.getImage(f.posterId);
            f.isFollowing = true;
        }
        return true;
    }

    private Map<String, String> getUsername(List<String> userIdList) throws Exception {
        Statement statement = null;
        try {
            String sql = "SELECT username, userId FROM copula.userProfile WHERE ";
            sql += SQLHelper.in("userId", userIdList);
            statement = mConnectionPool.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            Map<String, String> map = new HashMap<>();
            while (resultSet.next()) {
                String value = resultSet.getString("username");
                map.put(resultSet.getString("userId"), value);
            }
            return map;
        } finally {
            mConnectionPool.closeConnection(statement);
        }
    }//END
}
