package com.copulaapp.webservice.controllers.threads;

import com.copulaapp.webservice.models.threads.MediaThreadEntry;
import com.copulaapp.webservice.models.threads.MediaThreadsDao;
import com.copulaapp.webservice.util.Pager;
import com.copulaapp.webservice.util.RP;
import com.copulaapp.webservice.util.SecuredApi;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Created by heeleaz on 10/3/17.
 */
@Path("threads")
public class MediaThreadsController {
    private MediaThreadsDao threadsDao = new MediaThreadsDao();

    @PUT
    @Path("/newThread")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response putThread(MultivaluedMap<String, String> qp) throws Exception {
        MediaThreadEntry entry = new MediaThreadEntry();
        entry.album = qp.getFirst("album");
        entry.artist = qp.getFirst("artist");
        entry.title = qp.getFirst("title");
        entry.posterId = qp.getFirst("posterId");

        if ((entry.threadId = threadsDao.postThread(entry)) != null) {
            return RP.object(RP.SUCCESS, "Thread Posted", entry);
        } else return RP.string(RP.FAILED, "Thread Post Failed");
    }

    @GET
    @Path("/getThreads")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFriends(@Context UriInfo uriInfo) throws Exception {
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();
        String userId = qp.getFirst("userId");
        String limit = qp.getFirst("limit");
        Pager<MediaThreadEntry> p = threadsDao.getThreadTimeline(userId, limit);
        if (p != null && p.packet.size() > 0) {
            return RP.pager(RP.SUCCESS, "Threads Fetched", p);
        } else return RP.string(RP.EMPTY, "Threads not available");
    }//end
}
