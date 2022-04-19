package com.copulaapp.webservice.util;

import java.util.List;

/**
 * Created by heeleaz on 7/1/17.
 */
public class SQLHelper {
    public static String in(String column, List<String> values) {
        StringBuilder builder = new StringBuilder();
        builder.append(column).append(" IN(");
        for (String value : values) {
            builder.append('\'').append(value).append("\',");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.append(')').toString();
    }

    public static String eq(String column, String value) {
        return column + "=" + '\'' + value + '\'';
    }

    public static String eq(String column, int value) {
        return column + "=" + value;
    }
}
