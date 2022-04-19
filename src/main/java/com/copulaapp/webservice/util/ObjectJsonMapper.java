package com.copulaapp.webservice.util;


import com.copulaapp.webservice.util.annotations.Column;
import com.copulaapp.webservice.util.annotations.Transient;

import javax.json.*;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.Collection;

/**
 * Created by eliasigbalajobi on 2/28/16.
 */
public class ObjectJsonMapper {
    private static JsonObject unMapObject(Object object) {
        if (object == null) return null;

        Field[] fields = object.getClass().getFields();
        if (fields == null || fields.length <= 0) return null;

        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (Field field : fields) {
            try {
                resolve(object, field, builder);
            } catch (Exception e) {
            }
        }
        return builder.build();
    }

    private static void resolve(Object obj, Field field, JsonObjectBuilder jb)
            throws IllegalAccessException {
        Transient transAnnotation = field.getAnnotation(Transient.class);
        if (transAnnotation != null) {
            if (transAnnotation.valueType().equals("JSON")) return;
        }

        String fieldName = field.getName();
        Class<?> fieldType = field.getType();

        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            fieldType = (column.type() != Object.class) ? column.type() : field.getType();
        }

        Object r = field.get(obj);
        if (fieldType == String.class) jb.add(fieldName, String.valueOf(r));
        else if (fieldType == Integer.class) jb.add(fieldName, (Integer) r);
        else if (fieldType == Long.class) jb.add(fieldName, (Long) r);
        else if (fieldType == Date.class) jb.add(fieldName, r.toString());
        else if (fieldType == Boolean.class) jb.add(fieldName, (Boolean) r);
    }

    public static JsonValue toJson(Object object) {
        if (object == null) return null;

        if (object instanceof Collection) {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (Object o : (Collection) object) {
                JsonObject jsonObject = unMapObject(o);
                if (jsonObject != null) builder.add(jsonObject);
            }
            return builder.build();
        } else return unMapObject(object);
    }

    public static String toJsonString(Object object) {
        JsonValue j = toJson(object);
        if (j != null) return j.toString();
        else return "{}";
    }

    public static JsonObject readJson(String json) {
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jo = jsonReader.readObject();
        jsonReader.close();
        return jo;
    }
}