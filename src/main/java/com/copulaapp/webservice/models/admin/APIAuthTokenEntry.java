package com.copulaapp.webservice.models.admin;

import com.copulaapp.webservice.util.annotations.Column;

import java.sql.Date;

/**
 * Created by heeleaz on 11/4/16.
 */
public class APIAuthTokenEntry {
    public String username, apiToken;

    @Column(name = "expiresOn", type = Date.class)
    public Date expiresOn;
}
