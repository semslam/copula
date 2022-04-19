package com.copulaapp.webservice.models.threads;

import com.copulaapp.webservice.dbcon.CP30MysqlPooling;
import com.copulaapp.webservice.models.DaoException;
import com.copulaapp.webservice.models.feed.FollowerContentDao;
import com.copulaapp.webservice.models.feed.UserContentDao;
import com.copulaapp.webservice.models.profile.binder.PosterContentBinder;
import com.copulaapp.webservice.util.CopulaDate;
import com.copulaapp.webservice.util.DaoModelMapper;
import com.copulaapp.webservice.util.Pager;
import com.copulaapp.webservice.util.SQLHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heeleaz on 10/2/17.
 */
public class MediaThreadsDao {
    protected static final String TABLE = "copula.mediaThread";

    //unique content id identifying user content as Thread
    private static final int TYPE_ID = 13;

    private CP30MysqlPooling mConnectionPool = CP30MysqlPooling.getInstance();
    private UserContentDao userLineDAO = new UserContentDao();
    private FollowerContentDao followerContentDao = new FollowerContentDao();

    public String postThread(MediaThreadEntry push) throws SQLException, DaoException {
        try (Connection connection = mConnectionPool.getConnection()) {
            String sql = "INSERT INTO " + TABLE;
            sql += "(posterId,title,artist,album,activity,mediaId,createdAt)";
            sql += "VALUES(?,?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, push.posterId);
            statement.setString(2, push.title);
            statement.setString(3, push.artist);
            statement.setString(4, push.album);
            statement.setString(5, push.activity);
            statement.setString(6, push.mediaId);
            statement.setString(7, CopulaDate.getCurrentTime());

            String threadId = null;
            if (statement.executeUpdate() != 0) {
                String sqlLastId = "SELECT LAST_INSERT_ID()";
                ResultSet rs = connection.createStatement().executeQuery(sqlLastId);
                if (rs.next()) threadId = rs.getString(1);
            } else throw new DaoException("Thread Upload Failed");

            if (userLineDAO.putContent(push.posterId, TYPE_ID, threadId)) {
                followerContentDao.pushToFollowersAsync(TYPE_ID, threadId, push.posterId);
                return threadId;
            } else return null;
        }
    }

    public Pager<MediaThreadEntry> getThreadTimeline(String userId, String limit)
            throws Exception {
        if (limit == null || limit.equals("")) limit = "0,20";

        Pager<String> tids = followerContentDao.getContentId(userId, limit);
        if (tids == null) return null;//no threads available for this user

        List<MediaThreadEntry> ml = getThreadList(tids.packet, userId);
        if (ml != null) {
            return new Pager<>(ml, tids.getCurrentPageToken());
        } else return null;
    }

    public List<MediaThreadEntry> getThreadList(List<String> threadIds, String peeperId)
            throws Exception {
        if (threadIds == null || threadIds.size() == 0) return null;
        String sql = "SELECT * FROM " + TABLE + " WHERE ";
        sql += SQLHelper.in("threadId", threadIds) + " ORDER BY createdAt DESC";

        try (Statement stmts = mConnectionPool.getConnection().createStatement()) {
            ArrayList<MediaThreadEntry> entries = new ArrayList<>();

            ResultSet result = stmts.executeQuery(sql);
            while (result.next()) {
                entries.add(DaoModelMapper.map(result, MediaThreadEntry.class));
            }

            if (entries.size() > 0) {
                PosterContentBinder pcBinder = new PosterContentBinder();
                pcBinder.bindPosterProfile(entries);
            }
            return entries;
        }
    }
}