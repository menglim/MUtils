package org.suorsdey;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@UtilityClass
public class PropertyUtils {
    private static Properties properties;

    private Object getValue(String configProfile, String key, String env) {
        try {
            InputStream inputStream = null;
            if (configProfile == null) {
                if (env == null || env.equalsIgnoreCase("")) {
                    inputStream = PropertyUtils.class.getClassLoader().getResourceAsStream("application.properties");
                } else {
                    inputStream = PropertyUtils.class.getClassLoader().getResourceAsStream("application-" + env + ".properties");
                }

            } else {
                inputStream = new FileInputStream(configProfile);
            }
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e.fillInStackTrace());
            e.printStackTrace();
        }
        if (properties != null) {
            if (properties.containsKey(key)) {
                return properties.get(key);
            } else {
                log.warn("Key " + key + " not found. Returning NULL value");
            }
        }
        return null;
    }

    public String getString(String configProfile, String key) {
        return getString(configProfile, key, "");
    }

    public String getString(String configProfile, String key, String env) {
        return getValue(configProfile, key, env) == null ? null : getValue(configProfile, key, env).toString();
    }

    public int getInt(String configProfile, String key) {
        return Integer.parseInt(getString(configProfile, key) == null ? "0" : getString(configProfile, key));
    }

    public int getInt(String configProfile, String key, String env) {
        return Integer.parseInt(getString(configProfile, key, env) == null ? "0" : getString(configProfile, key, env));
    }

    public Long getLong(String configProfile, String key, String env) {
        return Long.parseLong(getString(configProfile, key, env) == null ? "0" : getString(configProfile, key, env));
    }

    public String[] getArrayOfString(String configProfile, String key, String separator, String env) {
        String values = getString(configProfile, key, env);
        return values == null ? null : values.split(separator);
    }

    public String[] getArrayOfString(String configProfile, String key) {
        return getArrayOfString(configProfile, key, ",", "");
    }

    public String[] getArrayOfString(String configProfile, String key, String env) {
        return getArrayOfString(configProfile, key, ",", env);
    }
}
