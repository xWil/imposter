package gg.wil.imposter.game;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import gg.wil.imposter.exception.message.InvalidDataException;
import gg.wil.imposter.util.ImposterUtil;

import java.util.Locale;

public record IconData(Shape shape, String shapeColor, String backgroundColor, String strokeColor, int strokeWidth) {

    public static int MAX_STROKE_SIZE = 20;
    public static int MIN_STROKE_SIZE = 0;

    public static IconData create(String shapeString, String shapeColor, String backgroundColor, String strokeColor, int strokeSize) throws IllegalArgumentException {
        Shape shape;
        try {
            shape = Shape.valueOf(shapeString.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new InvalidDataException("Field 'shape' contains an invalid shape", ex);
        }
        if(!ImposterUtil.checkValidHexColorCode(shapeColor)) throw new InvalidDataException("Field 'shapeColor' contains an invalid color code");
        if(!ImposterUtil.checkValidHexColorCode(backgroundColor)) throw new InvalidDataException("Field 'shapeColor' contains an invalid color code");
        if(!ImposterUtil.checkValidHexColorCode(strokeColor)) throw new InvalidDataException("Field 'strokeColor' contains an invalid color code");
        if(strokeSize > MAX_STROKE_SIZE || strokeSize < MIN_STROKE_SIZE) throw new InvalidDataException("Field 'strokeSize' contains an invalid value, must be between " + MIN_STROKE_SIZE + " and " +  MAX_STROKE_SIZE);

        return new IconData(shape, shapeColor, backgroundColor, strokeColor, strokeSize);
    }

    public JsonElement toJsonElement() {
        return new Gson().toJsonTree(this);
    }

    public enum Shape {
        CIRCLE,
        HEART,
        HEXAGON,
        PENTAGON,
        RHOMBUS,
        SQUARE,
        STAR,
        TRIANGLE
    }
}
