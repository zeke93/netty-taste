package cn.hackcoder.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by linzhichao on 2017/4/18.
 */
public final class ConfigUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

    private static final String configName = "rest.properties";


    private static Properties properties = new Properties() {{
        try {
            load(new InputStreamReader(ConfigUtil.class.getClassLoader().getResourceAsStream(configName), "UTF-8"));
            logger.info("load config success");
        } catch (IOException e) {
            logger.error("load config fail", e);

        }
    }};


    public static int getInt(String str) {
        try {
            return Integer.parseInt(properties.getProperty(str));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long getLong(String str) {
        try {
            return Long.parseLong(properties.getProperty(str));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static double getDouble(String str) {
        try {
            return Double.parseDouble(properties.getProperty(str));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getString(String str) {
        try {
            return properties.getProperty(str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean getBoolean(String str) {
        try {
            return Boolean.parseBoolean(properties.getProperty(str));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(ConfigUtil.getString("server.port"));
    }
}
