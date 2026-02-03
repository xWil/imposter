package gg.wil.imposter.game;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public record IconData(String shape, String shapeColor, String backgroundColor, String strokeColor, int strokeWidth) {

    public JsonElement toJsonElement() {
        return new Gson().toJsonTree(this);
    }
}
