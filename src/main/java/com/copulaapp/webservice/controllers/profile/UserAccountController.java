package com.copulaapp.webservice.controllers.profile;

import com.copulaapp.webservice.models.profile.UserAccountDao;
import com.copulaapp.webservice.models.profile.entry.UserAccountEntry;
import com.copulaapp.webservice.util.PBKPasswordHash;
import com.copulaapp.webservice.util.RP;
import com.copulaapp.webservice.util.SecuredApi;
import com.copulaapp.webservice.util.validator.Val;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

/**
 * Created by heeleeaz on 6/28/16.
 */
@Path("account")
public class UserAccountController {
    private final UserAccountDao daoUserAccount = new UserAccountDao();

    private UserAccountEntry resolveGetUser(String user) {
        if (user.contains("@")) return daoUserAccount.getAccountWithEmail(user);
        else return daoUserAccount.getAccountWithUsername(user);
    }

    @PUT
    @Path("/newAccount")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response newAccount(MultivaluedMap<String, String> qp) throws Exception {
        UserAccountEntry model = new UserAccountEntry();
        model.username = Val.parseUsername(qp.getFirst("username").toLowerCase());
        model.email = Val.parseEmail(qp.getFirst("email")).toLowerCase();
        model.password = PBKPasswordHash.hashPassword(qp.getFirst("password"));
        model.pnToken = qp.getFirst("pnToken");

        model.accountType = UserAccountDao.getAccountType("native");
        if (daoUserAccount.isUsernameExists(model.username)) {
            return RP.string(RP.EXISTS, "Username already taken", null);
        } else if (daoUserAccount.isEmailExists(model.email)) {
            return RP.string(RP.EXISTS, "Email already exists", null);
        }

        if ((model.userId = daoUserAccount.newAccount(model)) != null) {
            ProfileCloudMSGAware.subscribeAllAsync(model.pnToken);
            return RP.object(RP.SUCCESS, "Registration successful", model);
        } else {
            return RP.string(RP.FAILED, "Registration failed", null);
        }
    }

    @PUT
    @Path("/newAccountThirdParty")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @SecuredApi
    public Response newAccount3p(MultivaluedMap<String, String> qp) throws Exception {
        UserAccountEntry model = new UserAccountEntry();
        model.accountType = UserAccountDao.getAccountType(qp.getFirst("accountType"));
        model.tpUserId = qp.getFirst("tpUserId");
        model.username = Val.parseUsername(qp.getFirst("username"));
        model.email = Val.parseEmail(qp.getFirst("email"));

        if (daoUserAccount.isUsernameExists(model.username)) {
            return RP.string(RP.EXISTS, "Username already taken", null);
        } else if (daoUserAccount.isEmailExists(model.email)) {
            return RP.string(RP.EXISTS, "Email already exists", null);
        }

        if ((model.userId = daoUserAccount.newAccount(model)) != null) {
            ProfileCloudMSGAware.subscribeAllAsync(model.pnToken);
            return RP.object(RP.SUCCESS, "Registration successful", model);
        } else {
            return RP.string(RP.FAILED, "Registration failed", null);
        }
    }

    @POST
    @Path("/authAccount")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response authAccount(MultivaluedMap<String, String> qp) {
        String user = Val.parseUser(qp.getFirst("user"));
        String password = qp.getFirst("password");

        UserAccountEntry model = resolveGetUser(user);
        if (model == null) {
            String msg = "Username or email does not exists";
            return RP.string(RP.EMPTY, msg, null);
        } else if (PBKPasswordHash.validate(password, model.password)) {
            return RP.object(RP.SUCCESS, "Authentication Successful", model);
        } else {
            return RP.string(RP.FAILED, "Incorrect Login Details", null);
        }
    }

    @POST
    @Path("/authAccountThirdParty")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    @SecuredApi
    public Response authAccount3p(MultivaluedMap<String, String> qp) {
        String tpUserId = qp.getFirst("tpUserId");
        String accountType = qp.getFirst("accountType");

        UserAccountEntry model = daoUserAccount.getThirdPartyAccount(tpUserId);
        int at = UserAccountDao.getAccountType(accountType);
        if (model != null && model.accountType == at) {
            return RP.object(RP.SUCCESS, "Authentication Successful", model);
        } else {
            return RP.string(RP.FAILED, "Authentication Failed", null);
        }
    }

    @GET
    @Path("/getAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@QueryParam("userId") String userId) {
        UserAccountEntry entry = daoUserAccount.getAccount(userId);
        if (entry != null) {
            return RP.object(RP.SUCCESS, "Success", entry);
        } else return RP.string(RP.EMPTY, "Failed", null);
    }

    @GET
    @Path("/getAccountWithUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountWithUser(@QueryParam("user") String user) {
        UserAccountEntry model = resolveGetUser(user);
        if (model != null) {
            return RP.object(RP.SUCCESS, "Success", model);
        } else {
            return RP.string(RP.EMPTY, "Account does not exists", null);
        }
    }

    @POST
    @Path("/updateUsername")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @SecuredApi
    public Response updateUsername(MultivaluedMap<String, String> qp) throws SQLException {
        String userId = qp.getFirst("userId");
        String username = qp.getFirst("username");

        UserAccountEntry model = daoUserAccount.getAccount(userId);
        if (model == null) {//account does not exist..no update
            return RP.string(RP.FAILED, "Authentication Failed", null);
        } else if (daoUserAccount.getAccountWithUsername(username) != null) {
            return RP.string(RP.EXISTS, "Username not available", null);
        } else {
            model.username = username;
            if (daoUserAccount.updateUsername(userId, username)) {
                return RP.object(RP.SUCCESS, "Update successful", model);
            }
            return RP.string(RP.SUCCESS, "Update failed", null);
        }
    }

    @POST
    @Path("/updatePassword")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response updatePassword(MultivaluedMap<String, String> qp) throws Exception {
        String cPassword = qp.getFirst("currentPassword");
        String nPassword = qp.getFirst("newPassword");
        String userId = qp.getFirst("userId");

        UserAccountEntry model = daoUserAccount.getAccount(userId);
        if (model == null) {
            return RP.string(RP.EMPTY, "Account not found", null);
        } else if (!PBKPasswordHash.validate(cPassword, model.password)) {
            return RP.string(RP.FAILED, "Authentication failed", null);
        }

        String hashPassword = PBKPasswordHash.hashPassword(nPassword);
        if (daoUserAccount.updatePassword(userId, hashPassword)) {
            return RP.string(RP.SUCCESS, "Update successful", null);
        } else {
            return RP.string(RP.FAILED, "Update failed", null);
        }
    }

    @POST
    @Path("/updatePNToken")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response updatePNToken(MultivaluedMap<String, String> qp) throws Exception {
        String userId = qp.getFirst("userId");
        String pnToken = qp.getFirst("pnToken");

        UserAccountEntry model = daoUserAccount.getAccount(userId);
        if (model == null) {
            return RP.string(RP.EMPTY, "Account not found", null);
        }

        if (daoUserAccount.updatePNToken(userId, pnToken)) {
            ProfileCloudMSGAware.subscribeAllAsync(pnToken);
            return RP.string(RP.SUCCESS, "Successful", null);
        } else {
            return RP.string(RP.FAILED, "Failed", null);
        }
    }

    @GET
    @Path("/forgetPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgetPassword(@QueryParam("userId") int userId) {
        return null;
    }
}
