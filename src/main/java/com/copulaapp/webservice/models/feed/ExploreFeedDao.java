package com.copulaapp.webservice.models.feed;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.models.profile.UserInterestDao;
import com.copulaapp.webservice.models.profile.binder.FeedContentBinder;
import com.copulaapp.webservice.models.profile.binder.PosterContentBinder;
import com.copulaapp.webservice.util.DaoModelMapper;
import com.copulaapp.webservice.util.Pager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heeleaz on 9/22/17.
 */
public class ExploreFeedDao extends FeedBucketDao {
    private CP30MysqlPooling mDBConnection = CP30MysqlPooling.getInstance();
    private UserInterestDao userInterestDao = new UserInterestDao();

    public Pager<FeedEntry> getExploreFeeds(String userId, String limit) throws Exception {
        PreparedStatement statement = null;
        try {
            if (limit == null || limit.equals("")) limit = "0,20";

            statement = this.buildInterestStmt(userId, limit);
            ResultSet resultSet = statement.executeQuery();

            List<FeedEntry> entries = new ArrayList<>();
            while (resultSet.next()) {
                entries.add(DaoModelMapper.map(resultSet, FeedEntry.class));
            }

            new PosterContentBinder().bindPosterProfile(entries);
            FeedContentBinder fcBinder = new FeedContentBinder<>(entries);
            fcBinder.bindLikeRegister(userId);
            fcBinder.bindFeedContentFiles();
            return new Pager<>(joinUserProfile(userId, entries), limit);
        } finally {
            mDBConnection.closeConnection(statement);
        }
    }

    private PreparedStatement buildInterestStmt(String userId, String limit) throws Exception {
        String interest = userInterestDao.getUserInterest(userId);
        String sql = "SELECT * FROM " + TABLE + " WHERE "
                + "JSON_CONTAINS(?, tag) OR JSON_CONTAINS(tag, ?)"
                + "ORDER BY feedId DESC LIMIT " + limit;

        PreparedStatement statement =
                mDBConnection.getConnection().prepareStatement(sql);
        statement.setString(1, interest);
        statement.setString(2, interest);
        return statement;
    }
}
