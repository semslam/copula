package com.copulaapp.messaging.mailer;

/**
 * Created by heeleaz on 9/9/17.
 */
public class DefaultConfig extends Configuration {
    @Override
    public String getHost() {
        return "email-smtp.us-west-2.amazonaws.com";
    }

    @Override
    public String getUsername() {
        return "AKIAJZT3MWGBKZQC35CQ";
    }

    @Override
    public String getPassword() {
        return "AvojCwdADceQen4PM5vcF4DCqpFSdW80/YUWAALPiLNT";
    }

    @Override
    public int getPort() {
        return 25;
    }

    @Override
    public boolean authenticate() {
        return true;
    }

    @Override
    public boolean supportTTL() {
        return true;
    }
}
