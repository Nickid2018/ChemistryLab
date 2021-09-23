package com.github.nickid2018.chemistrylab.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class BasicText implements Text {

    protected final List<Text> args = new ArrayList<>();
    protected boolean translate;
    protected String value;

    public static BasicText newTranslateText(String value, Object... args) {
        BasicText text = new BasicText();
        text.translate = true;
        text.value = value;
        for (Object arg : args)
            text.args.add(newLiteralText(arg.toString()));
        return text;
    }


    public static BasicText newTranslateText(String value, Text... args) {
        BasicText text = new BasicText();
        text.translate = true;
        text.value = value;
        text.args.addAll(List.of(args));
        return text;
    }

    public static BasicText newLiteralText(String value, Object... args) {
        BasicText text = new BasicText();
        text.translate = false;
        text.value = value;
        for (Object arg : args)
            text.args.add(newLiteralText(arg.toString()));
        return text;
    }

    public static BasicText newLiteralText(String value, Text... args) {
        BasicText text = new BasicText();
        text.translate = false;
        text.value = value;
        text.args.addAll(List.of(args));
        return text;
    }

    public String getValue() {
        return value;// to do
    }

    @Override
    public void serialize(JsonObject obj) {
        obj.addProperty("translate", translate);
        obj.addProperty("value", value);
        JsonArray array = new JsonArray();
        for (Text now : args) {
            JsonObject object = new JsonObject();
            Text.serializeJSON(now, object);
            array.add(object);
        }
        obj.add("args", array);
    }

    @Override
    public void deserialize(JsonObject json) {
        translate = json.get("translate").getAsBoolean();
        value = json.get("value").getAsString();
        for (JsonElement element : json.getAsJsonArray("args"))
            args.add(Text.deserializeJSON(element.getAsJsonObject()));
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public boolean needTranslate() {
        return translate;
    }
}
