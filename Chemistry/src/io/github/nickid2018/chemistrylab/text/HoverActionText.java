package io.github.nickid2018.chemistrylab.text;

import com.google.gson.JsonObject;

public class HoverActionText extends BasicText implements Actionable {

    private Text info;

    public static HoverActionText newTranslateHoverText(String value, Text info) {
        HoverActionText text = new HoverActionText();
        text.translate = true;
        text.value = value;
        text.info = info;
        return text;
    }

    public static HoverActionText newLiteralHoverText(String value, Text info) {
        HoverActionText text = new HoverActionText();
        text.translate = false;
        text.value = value;
        text.info = info;
        return text;
    }

    @Override
    public void serialize(JsonObject obj) {
        super.serialize(obj);
        JsonObject object = new JsonObject();
        Text.serializeJSON(info, object);
        obj.add("info", object);
    }

    @Override
    public void deserialize(JsonObject json) {
        super.deserialize(json);
        info = Text.deserializeJSON(json.getAsJsonObject("info"));
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public ActionType getType() {
        return ActionType.HOVER;
    }

    public Text getInfo() {
        return info;
    }
}
