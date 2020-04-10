package com.github.nickid2018.chemistrylab.init;

import java.util.*;
import com.badlogic.gdx.graphics.*;
import com.github.mmc1234.pinkengine.*;

public class LoadingWindowProgress {

	private final Box box;
	private final Stack<ProgressEntry> progresses = new Stack<>();

	LoadingWindowProgress(Box box) {
		this.box = box;
	}

	public ProgressEntry push(int size) {
		ProgressEntry entry = new ProgressEntry();
		entry.progress = new SimpleProgressBar(size, UIStyles.PROGRESSBAR_BACKGROUND, UIStyles.PROGRESSBAR_FOREGROUND);
		entry.progress.setPosition(100, 430 + progresses.size() * 80 + 25);
		entry.progress.setSize(1080, 28);
		entry.message = new Text2D(UIStyles.FONT);
		entry.message.setPosition(100, 430 + progresses.size() * 80);
		entry.message.setColor(Color.BLACK);
		entry.message.getInfo().setSize(23);
		progresses.push(entry);
		box.add(entry.message);
		box.add(entry.progress);
		return entry;
	}

	public void pop() {
		ProgressEntry entry = progresses.pop();
		box.remove(entry.message);
		box.remove(entry.progress);
	}

	public static final class ProgressEntry {
		public SimpleProgressBar progress;
		public Text2D message;
	}
}
