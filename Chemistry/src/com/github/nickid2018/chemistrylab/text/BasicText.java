package com.github.nickid2018.chemistrylab.text;

import com.google.gson.*;

public class BasicText implements Text {

	protected boolean translate;
	protected String value;

	public static BasicText newTranslateText(String value) {
		BasicText text = new BasicText();
		text.translate = true;
		text.value = value;
		return text;
	}

	public static BasicText newLiteralText(String value) {
		BasicText text = new BasicText();
		text.translate = false;
		text.value = value;
		return text;
	}

	public String getValue() {
		return value;// to do
	}

	@Override
	public void serialize(JsonObject obj) {
		obj.addProperty("translate", translate);
		obj.addProperty("value", value);
	}

	@Override
	public void deserialize(JsonObject json) {
		translate = json.get("translate").getAsBoolean();
		value = json.get("value").getAsString();
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
