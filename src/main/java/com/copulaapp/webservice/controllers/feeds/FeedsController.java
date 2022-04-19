package com.copulaapp.webservice.controllers.feeds;

import com.copulaapp.mediaws.FeedMediaManager;
import com.copulaapp.webservice.models.feed.ExploreFeedDao;
import com.copulaapp.webservice.models.feed.FeedBucketDao;
import com.copulaapp.webservice.models.feed.FeedEntry;
import com.copulaapp.webservice.models.feed.TimelineFeedDao;
import com.copulaapp.webservice.models.feed.notification.ExploreAnnouncer;
import com.copulaapp.webservice.models.feed.notification.FeedReactAnnouncer;
import com.copulaapp.webservice.models.profile.UserProfileDao;
import com.copulaapp.webservice.models.profile.entry.UserProfileEntry;
import com.copulaapp.webservice.util.Pager;
import com.copulaapp.webservice.util.RP;
import com.copulaapp.webservice.util.SecuredApi;
import com.copulaapp.webservice.util.validator.Val;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by heeleeaz on 6/28/16.
 */
@Path("feeds")
public class FeedsController {
    private static Logger logger = Logger.getLogger("FeedsController");

    private FeedBucketDao feedBucketDao = new FeedBucketDao();
    private ExploreFeedDao exploreFeedDao = new ExploreFeedDao();
    private TimelineFeedDao timelineFeedDao = new TimelineFeedDao();
    private UserProfileDao userProfileDAO = new UserProfileDao();

    @PUT
    @Path("/postFeed")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response postFeed(MultivaluedMap<String, String> qp) throws Exception {
        FeedEntry model = new FeedEntry();
        model.posterId = qp.getFirst("posterId");
        model.feedType = Integer.parseInt(qp.getFirst("feedType"));
        model.mediaType = Integer.parseInt(qp.getFirst("mediaType"));
        model.mediaTitle = Val.parseText(qp.getFirst("mediaTitle"));
        model.mediaCaption = Val.parseText(qp.getFirst("mediaCaption"));
        model.mediaMeta = Val.parseText(qp.getFirst("mediaMeta"));
        String uploadMediaId = qp.getFirst("uploadMediaId");

        if ((model.feedId = feedBucketDao.postFeed(model)) != null) {
            FeedMediaManager.rename(uploadMediaId, model.feedId, model.mediaType);
            model = feedBucketDao.getFeed(model.feedId, model.posterId);
            new ExploreAnnouncer().sendToAllInterestUsersAsync(model);
            return RP.object(RP.SUCCESS, "Feed posted", model);
        } else return RP.string(RP.FAILED, "Post Failed");
    }

    @GET
    @Path("/getExploreFeeds")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExploreFeeds(@Context UriInfo uriInfo) throws Exception {
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();
        String posterId = qp.getFirst("userId");
        String pageToken = qp.getFirst("pageToken");

        Pager<FeedEntry> e = exploreFeedDao.getExploreFeeds(posterId, pageToken);
        if (e != null && e.packet.size() > 0) {
            return RP.pager(RP.SUCCESS, "Success", e);
        } else {
            return RP.object(RP.EMPTY, "NULL Object", null);
        }
    }

    @GET
    @Path("/getFeed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeed(@Context UriInfo uriInfo) throws Exception {
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();
        String peeperId = qp.getFirst("peeperId");
        String feedId = qp.getFirst("feedId");

        FeedEntry model = feedBucketDao.getFeed(feedId, peeperId);
        if (model != null) {
            return RP.object(RP.SUCCESS, "Success", model);
        } else return RP.string(RP.EMPTY, "Feed Not Found!!", null);
    }

    @GET
    @Path("/getFeeds")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeeds(@Context UriInfo uriInfo) throws Exception {
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();
        String posterId = qp.getFirst("posterId");
        String peeperId = qp.getFirst("peeperId");
        String pageToken = qp.getFirst("pageToken");

        Pager<FeedEntry> e = feedBucketDao.getUserFeeds(posterId, peeperId, pageToken);
        if (e != null && e.packet.size() > 0) {
            return RP.pager(RP.SUCCESS, "Success", e);
        } else {
            return RP.object(RP.EMPTY, "NULL Object", null);
        }
    }

    @GET
    @Path("/getFeedTimeline")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeedTimeline(@Context UriInfo uriInfo) throws Exception {
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();
        String posterId = qp.getFirst("userId");
        String pageToken = qp.getFirst("pageToken");

        Pager<FeedEntry> e = timelineFeedDao.getTimeLineFeeds(posterId, pageToken);
        if (e != null && e.packet.size() > 0) {
            return RP.pager(RP.SUCCESS, "Success", e);
        } else {
            return RP.object(RP.EMPTY, "NULL Object", null);
        }
    }

    @POST
    @Path("/likeFeed")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response likeFeed(MultivaluedMap<String, String> qp) throws SQLException {
        String feedId = qp.getFirst("feedId");
        String likerId = qp.getFirst("likerId");

        if (feedBucketDao.isFeedLiked(feedId, likerId)) {
            return RP.string(RP.SUCCESS, "Success", null);
        } else if (feedBucketDao.likeFeed(feedId, likerId)) {
            try {
                FeedEntry feed = feedBucketDao.getFeed(feedId, likerId);
                String pnToken = userProfileDAO.getPNToken(feed.posterId);
                UserProfileEntry liker = userProfileDAO.getProfile(likerId);
                FeedReactAnnouncer.pushFeedLikeAsync(pnToken, liker, feed);
            } catch (Exception e) {
                logger.log(Level.WARNING, "PushFeedLike Failed: " + e);
            }
            return RP.string(RP.SUCCESS, "Success", null);
        } else {
            return RP.string(RP.FAILED, "Failed", null);
        }
    }

    @POST
    @Path("/unlikeFeed")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response unlikeMedia(MultivaluedMap<String, String> qp) throws SQLException {
        String feedId = qp.getFirst("feedId");
        String likerId = qp.getFirst("likerId");

        if (feedBucketDao.unlikeFeed(feedId, likerId)) {
            return RP.string(RP.SUCCESS, "Success", null);
        } else return RP.string(RP.FAILED, "Failed", null);
    }

    @DELETE
    @Path("/removeFeed")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response removeFeed(MultivaluedMap<String, String> qp) throws Exception {
        String feedId = qp.getFirst("feedId");
        String posterId = qp.getFirst("posterId");

        if (feedBucketDao.delFeed(feedId, posterId)) {
            return RP.string(RP.SUCCESS, "Success", null);
        } else return RP.string(RP.FAILED, "Failed", null);
    }

    @DELETE
    @Path("/removeFeeds")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response removeFeeds(MultivaluedMap<String, String> qp) throws SQLException {
        String posterId = qp.getFirst("posterId");
        if (feedBucketDao.delFeeds(posterId)) {
            return RP.string(RP.SUCCESS, "Success", null);
        } else {
            return RP.string(RP.FAILED, "Failed", null);
        }
    }
}
