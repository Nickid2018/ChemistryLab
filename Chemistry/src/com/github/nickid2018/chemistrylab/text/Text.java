package com.github.nickid2018.chemistrylab.text;

import java.util.*;
import com.google.gson.*;

public interface Text {

	public static final List<Class<? extends Text>> TYPES = Arrays.asList(BasicText.class, ClickActionText.class,
			HoverActionText.class);

	public String getValue();

	public void serialize(JsonObject json);

	public void deserialize(JsonObject json);

	public int getId();

	public boolean needTranslate();

	public static String serialize(Text text) {
		JsonObject obj = new JsonObject();
		serializeJSON(text, obj);
		return obj.toString();
	}

	public static void serializeJSON(Text text, JsonObject obj) {
		obj.addProperty("id", text.getId());
		text.serialize(obj);
	}

	public static Text deserialize(String json) {
		JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
		return deserializeJSON(obj);
	}

	public static Text deserializeJSON(JsonObject obj) {
		try {
			Text text = TYPES.get(obj.get("id").getAsInt()).newInstance();
			text.deserialize(obj);
			return text;
		} catch (Exception e) {
			throw new IllegalArgumentException("Bad serialized text", e);
		}
	}
}
