package com.github.nickid2018.chemistrylab.text;

import java.util.*;
import com.google.gson.*;
import com.google.common.collect.*;

public class ComplexText implements Text {

	private List<Text> content = Lists.newArrayList();

	public static ComplexText newComplexText(Text... texts) {
		ComplexText text = new ComplexText();
		for (Text t : texts) {
			if (t instanceof ComplexText)
				text.content.addAll(((ComplexText) t).content);
			else
				text.content.add(t);
		}
		return text;
	}

	@Override
	public String getValue() {
		StringBuilder sb = new StringBuilder();
		content.forEach(text -> sb.append(text.getValue()));
		return sb.toString();
	}

	@Override
	public void serialize(JsonObject obj) {
		JsonArray array = new JsonArray();
		for (Text text : content) {
			JsonObject object = new JsonObject();
			Text.serializeJSON(text, object);
			array.add(object);
		}
		obj.add("content", array);
	}

	@Override
	public void deserialize(JsonObject json) {
		JsonArray array = json.getAsJsonArray("content");
		array.forEach(element -> content.add(Text.deserializeJSON(element.getAsJsonObject())));
	}

	@Override
	public int getId() {
		return 3;
	}

	public List<Text> getContent() {
		return content;
	}

	@Override
	public boolean needTranslate() {
		return false;
	}
}
