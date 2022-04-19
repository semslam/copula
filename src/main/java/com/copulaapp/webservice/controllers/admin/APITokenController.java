package com.copulaapp.webservice.controllers.admin;

import com.copulaapp.webservice.models.admin.APIAuthTokenDao;
import com.copulaapp.webservice.models.admin.APIAuthTokenEntry;
import com.copulaapp.webservice.models.admin.AdminAccountDao;
import com.copulaapp.webservice.models.admin.AdminAccountEntry;
import com.copulaapp.webservice.util.PBKPasswordHash;
import com.copulaapp.webservice.util.RP;
import com.copulaapp.webservice.util.validator.Val;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by heeleeaz on 7/24/16.
 */
@Path("api")
public class APITokenController {
    private static APIAuthTokenDao mTokenStore = new APIAuthTokenDao();
    private AdminAccountDao adminAccountDao = new AdminAccountDao();

    private static String bindUserWithToken(String user, String token) {
        return user + "-" + token;
    }

    private static String[] unBindToken(String token) {
        String[] splitted = token.split("-");
        return (splitted.length > 1) ? splitted : null;
    }

    public static boolean authToken(String token) throws Exception {
        String[] tokenBreak = unBindToken(token);
        if (tokenBreak == null) return false;

        APIAuthTokenEntry tokenModel = mTokenStore.getToken(tokenBreak[0]);
        return (tokenModel != null && tokenModel.apiToken.equals(tokenBreak[1]));
    }

    @PUT
    @Path("/nxtempAccount")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newAccount(MultivaluedMap<String, String> qp) throws Exception {
        String user = Val.parseUsername(qp.getFirst("user"));
        String password = PBKPasswordHash.hashPassword(qp.getFirst("password"));

        if ((adminAccountDao.newAccount(user, password))) {
            return RP.string(RP.SUCCESS, "Registration successful", null);
        } else {
            return RP.string(RP.FAILED, "Registration failed", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/authenticate")
    public Response authenticate(MultivaluedMap<String, String> qp) throws Exception {
        String user = qp.getFirst("user");
        String password = qp.getFirst("password");

        AdminAccountEntry model = adminAccountDao.getAccount(user);
        if (model == null) {
            return RP.string(RP.EMPTY, "Account does not exists", null);
        } else if (!PBKPasswordHash.validate(password, model.password)) {
            return RP.string(RP.FAILED, "Invalid account details", null);
        } else {
            long expiresIn = 3600 * 1000;//create random token to end in an hour
            APIAuthTokenEntry tokenGen = this.createToken(user, expiresIn);
            if (tokenGen != null) {
                return RP.object(RP.SUCCESS, "Token generated", tokenGen);
            } else {
                return RP.string(RP.SUCCESS, "Token generate failed", null);
            }
        }
    }

    private APIAuthTokenEntry createToken(String user, long expiresIn) throws Exception {
        APIAuthTokenEntry t = mTokenStore.getToken(user);
        if (t == null) {
            String token = new BigInteger(300, new SecureRandom()).toString(32);
            t = mTokenStore.newToken(user, token, expiresIn);
        }

        if (t != null) t.apiToken = bindUserWithToken(user, t.apiToken);
        return t;
    }
}
