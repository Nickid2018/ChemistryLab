package com.github.nickid2018.chemistrylab.text;

import com.google.gson.JsonObject;

public class ClickActionText extends BasicText implements Actionable {

    private ClickEvent event;
    private String info;

    public static ClickActionText newTranslateClickText(String value, ClickEvent event, String info) {
        ClickActionText text = new ClickActionText();
        text.translate = true;
        text.value = value;
        text.event = event;
        text.info = info;
        return text;
    }

    public static ClickActionText newLiteralClickText(String value, ClickEvent event, String info) {
        ClickActionText text = new ClickActionText();
        text.translate = false;
        text.value = value;
        text.event = event;
        text.info = info;
        return text;
    }

    @Override
    public void serialize(JsonObject obj) {
        super.serialize(obj);
        obj.addProperty("event", event.ordinal());
        obj.addProperty("info", info);
    }

    @Override
    public void deserialize(JsonObject json) {
        super.deserialize(json);
        event = ClickEvent.values()[json.get("event").getAsInt()];
        info = json.get("info").getAsString();
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public ActionType getType() {
        return ActionType.CLICK;
    }

    public ClickEvent getEvent() {
        return event;
    }

    public String getEventInfo() {
        return info;
    }

}
