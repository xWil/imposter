package gg.wil.imposter.game;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.NoSuchElementException;

public class Settings {

    private final JsonObject data;
    private final Map<String, Object> settings;

    public Settings(JsonObject data) {
        if(data == null) throw new NullPointerException("'data' cannot be null");
        this.data = data;
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        this.settings = new Gson().fromJson(data, type);
    }

    public boolean containsKey(String key) {
        return settings.containsKey(key);
    }

    public String getString(String key) {
        Object value = settings.get(key);
        if(value instanceof String string) {
            return string;
        } else {
            throw new NoSuchElementException(settings + " does not contain a String value for " + key);
        }
    }

    public String getStringOrDefault(String key, String defaultValue) {
        Object value = settings.get(key);
        if(value instanceof String string) {
            return string;
        } else {
            return defaultValue;
        }
    }

    public int getInt(String key) {
        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.intValue();
        } else {
            throw new NoSuchElementException(settings + " does not contain an Int value for " + key);
        }
    }

    public int getIntOrDefault(String key, int defaultValue) {
        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.intValue();
        } else {
            return defaultValue;
        }
    }

    public long getLong(String key) {
        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.longValue();
        } else {
            throw new NoSuchElementException(settings + " does not contain a Long value for " + key);
        }
    }

    public long getLongOrDefault(String key, long defaultValue) {
        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.longValue();
        } else {
            return defaultValue;
        }
    }

    public double getDouble(String key) {
        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.doubleValue();
        } else {
            throw new NoSuchElementException(settings + " does not contain a Double value for " + key);
        }
    }

    public double getDoubleOrDefault(String key, double defaultValue) {
        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.doubleValue();
        } else {
            return defaultValue;
        }
    }

    public float getFloat(String key) {
        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.floatValue();
        } else {
            throw new NoSuchElementException(settings + " does not contain a Float value for " + key);
        }
    }

    public float getFloat(String key, float defaultValue) {
        Object value = settings.get(key);
        if(value instanceof Number number) {
            return number.floatValue();
        } else {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key) {
        Object value = settings.get(key);
        if(value instanceof Boolean bool) {
            return bool;
        } else {
            throw new NoSuchElementException(settings + " does not contain a Boolean value for " + key);
        }
    }

    public boolean getBooleanOrDefault(String key, boolean defaultValue) {
        Object value = settings.get(key);
        if(value instanceof Boolean bool) {
            return bool;
        } else {
            return defaultValue;
        }
    }
}
