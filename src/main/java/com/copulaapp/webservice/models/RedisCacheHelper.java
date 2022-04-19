package com.copulaapp.webservice.models;

import com.copulaapp.webservice.Copula;
import com.sun.rowset.CachedRowSetImpl;
import redis.clients.jedis.Jedis;

import javax.sql.rowset.CachedRowSet;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by heeleaz on 9/12/17.
 */
public class RedisCacheHelper {
    public static CachedRowSet getCachedRowSet(String statement) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        Jedis jedis = null;

        try {
            jedis = new Jedis(Copula.REDIS_NODE);
            byte[] result = jedis.get(statement.getBytes());
            if (result == null) return null;

            bis = new ByteArrayInputStream(result);
            ois = new ObjectInputStream(bis);

            CachedRowSetImpl cachedRowSet = new CachedRowSetImpl();
            cachedRowSet.populate((CachedRowSet) ois.readObject());

            return cachedRowSet;
        } catch (ClassNotFoundException | IOException | SQLException e) {
            return null;
        } finally {
            try {
                if (bis != null) bis.close();
                if (ois != null) ois.close();
                if (jedis != null) jedis.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean setCachedRowSet(String statement, ResultSet rs, int expire) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream bos = null;
        Jedis jedis = null;
        try {
            CachedRowSet cachedRowSet = new CachedRowSetImpl();
            cachedRowSet.populate(rs, 1);

            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(cachedRowSet);

            byte[] key = statement.getBytes();
            jedis = new Jedis(Copula.REDIS_NODE);
            jedis.set(key, bos.toByteArray());
            jedis.expire(key, expire);
            return true;
        } catch (IOException | SQLException e) {
            return false;
        } finally {
            try {
                if (oos != null) oos.close();
                if (bos != null) bos.close();
                if (jedis != null) jedis.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean delCache(String cacheKey) {
        //Jedis jedis = new Jedis(Copula.REDIS_NODE);
        //jedis.del(cacheKey);
        //jedis.close();
        return true;
    }


    public static String getCacheString(String key) {
        //Jedis jedis = new Jedis(Copula.REDIS_NODE);
        //String result = jedis.get(key);
        //jedis.close();
        return null;
    }

    public static void setCacheString(String key, String value) {
        //Jedis jedis = new Jedis(Copula.REDIS_NODE);
        //jedis.set(key, value);
        //jedis.close();
    }
}
