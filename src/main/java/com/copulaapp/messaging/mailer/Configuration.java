package com.copulaapp.messaging.mailer;

/**
 * Created by heeleaz on 9/9/17.
 */
public abstract class Configuration {
    public abstract String getHost();

    public abstract String getUsername();

    public abstract String getPassword();

    public abstract int getPort();

    public abstract boolean authenticate();

    public abstract boolean supportTTL();
}
