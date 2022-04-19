package com.copulaapp.webservice.util;


/**
 * Created by heeleaz on 6/30/17.
 */
public class CopulaDate {
    public static String getCurrentTime() {
        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dt);
    }
}
