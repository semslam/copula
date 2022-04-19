package com.copulaapp.webservice.util;

import com.copulaapp.webservice.util.annotations.Column;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by heeleeaz on 7/4/16.
 */
public class DaoModelMapper {
    private static void resolve(Field field, Object obj, ResultSet row)
            throws IllegalAccessException, SQLException {
        Column column = field.getAnnotation(Column.class);

        Class<?> type;
        String name;
        if (column != null) {
            name = (column.name().equals("")) ? field.getName() : column.name();
            type = (column.type() == Object.class) ? field.getType() : column.type();
        } else {
            name = field.getName();
            type = field.getType();
        }

        int columnIndex;
        try {
            columnIndex = row.findColumn(name);
        } catch (SQLException e) {
            return;//column does not exists
        }

        if (type == Integer.class) field.set(obj, row.getInt(columnIndex));
        else if (type == Date.class) field.set(obj, row.getDate(columnIndex));
        else if (type == Long.class) field.set(obj, row.getLong(columnIndex));
        else field.set(obj, row.getString(columnIndex));
    }

    public static <T> T map(ResultSet row, Class<T> to) throws ModelMapperException {
        try {
            T obj = to.newInstance();
            for (Field field : to.getFields()) {
                try {
                    resolve(field, obj, row);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return obj;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ModelMapperException(e.getMessage());
        }
    }
}
