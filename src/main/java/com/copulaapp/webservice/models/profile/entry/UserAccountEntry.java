package com.copulaapp.webservice.models.profile.entry;

import com.copulaapp.webservice.util.annotations.Column;
import com.copulaapp.webservice.util.annotations.Transient;

import java.sql.Date;

/**
 * Created by heeleeaz on 8/3/16.
 */
public class UserAccountEntry {
    public String username, email, tpUserId, userId, pnToken;

    @Transient()//hide in json data
    public String password;

    @Column(type = Date.class, name = "createdAt")
    public Date createdAt;

    @Column(type = Date.class, name = "updatedAt")
    public Date updatedAt;

    @Column(name = "accountType", type = Integer.class)
    public int accountType;

    @Override
    public boolean equals(Object obj) {
        UserAccountEntry c = (UserAccountEntry) obj;
        return c.userId != null && c.userId.equals(this.userId);
    }
}//end
