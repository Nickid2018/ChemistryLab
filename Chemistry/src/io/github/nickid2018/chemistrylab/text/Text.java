package io.github.nickid2018.chemistrylab.text;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.List;

public interface Text {

    List<Class<? extends Text>> TYPES = Arrays.asList(BasicText.class, ClickActionText.class,
            HoverActionText.class);

    static String serialize(Text text) {
        JsonObject obj = new JsonObject();
        serializeJSON(text, obj);
        return obj.toString();
    }

    static void serializeJSON(Text text, JsonObject obj) {
        obj.addProperty("id", text.getId());
        text.serialize(obj);
    }

    static Text deserialize(String json) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        return deserializeJSON(obj);
    }

    static Text deserializeJSON(JsonObject obj) {
        try {
            Text text = TYPES.get(obj.get("id").getAsInt()).getDeclaredConstructor().newInstance();
            text.deserialize(obj);
            return text;
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad serialized text", e);
        }
    }

    String getValue();

    void serialize(JsonObject json);

    void deserialize(JsonObject json);

    int getId();

    boolean needTranslate();
}
