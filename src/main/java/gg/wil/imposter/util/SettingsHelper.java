package gg.wil.imposter.util;

import java.util.Map;
import java.util.NoSuchElementException;

public class SettingsHelper {

    public static boolean containsKey(Map<String, Object> settings, String key) {
        if(settings == null) throw new IllegalArgumentException("Settings cannot be null");

        return settings.containsKey(key);
    }

    public static String getString(Map<String, Object> settings, String key) {
        if(settings == null) throw new IllegalArgumentException("Settings cannot be null");

        Object value = settings.get(key);
        if(value instanceof String string) {
            return string;
        } else {
            throw new NoSuchElementException(settings + " does not contain a String value for " + key);
        }
    }

    public static String getStringOrDefault(Map<String, Object> settings, String key, String defaultValue) {
        if(settings == null) return defaultValue;

        Object value = settings.get(key);
        if(value instanceof String string) {
            return string;
        } else {
            return defaultValue;
        }
    }

    public static int getInt(Map<String, Object> settings, String key) {
        if(settings == null) throw new IllegalArgumentException("Settings cannot be null");

        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.intValue();
        } else {
            throw new NoSuchElementException(settings + " does not contain an Int value for " + key);
        }
    }

    public static int getIntOrDefault(Map<String, Object> settings, String key, int defaultValue) {
        if(settings == null) return defaultValue;

        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.intValue();
        } else {
            return defaultValue;
        }
    }

    public static long getLong(Map<String, Object> settings, String key) {
        if(settings == null) throw new IllegalArgumentException("Settings cannot be null");

        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.longValue();
        } else {
            throw new NoSuchElementException(settings + " does not contain a Long value for " + key);
        }
    }

    public static long getLongOrDefault(Map<String, Object> settings, String key, long defaultValue) {
        if(settings == null) return defaultValue;

        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.longValue();
        } else {
            return defaultValue;
        }
    }

    public static double getDouble(Map<String, Object> settings, String key) {
        if(settings == null) throw new IllegalArgumentException("Settings cannot be null");

        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.doubleValue();
        } else {
            throw new NoSuchElementException(settings + " does not contain a Double value for " + key);
        }
    }

    public static double getDoubleOrDefault(Map<String, Object> settings, String key, double defaultValue) {
        if(settings == null) return defaultValue;

        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.doubleValue();
        } else {
            return defaultValue;
        }
    }

    public static float getFloat(Map<String, Object> settings, String key) {
        if(settings == null) throw new IllegalArgumentException("Settings cannot be null");

        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.floatValue();
        } else {
            throw new NoSuchElementException(settings + " does not contain a Float value for " + key);
        }
    }

    public static float getFloat(Map<String, Object> settings, String key, float defaultValue) {
        if(settings == null) return defaultValue;

        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.floatValue();
        } else {
            return defaultValue;
        }
    }

    public static boolean getBoolean(Map<String, Object> settings, String key) {
        if(settings == null) throw new IllegalArgumentException("Settings cannot be null");

        Object value = settings.get(key);
        if(value instanceof Boolean bool) {
            return bool;
        } else {
            throw new NoSuchElementException(settings + " does not contain a Boolean value for " + key);
        }
    }

    public static boolean getBooleanOrDefault(Map<String, Object> settings, String key, boolean defaultValue) {
        if(settings == null) return defaultValue;

        Object value = settings.get(key);
        if(value instanceof Boolean bool) {
            return bool;
        } else {
            return defaultValue;
        }
    }
}
