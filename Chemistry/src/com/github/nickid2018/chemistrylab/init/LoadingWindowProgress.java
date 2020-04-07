package com.github.nickid2018.chemistrylab.init;

import java.util.*;
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
		entry.progress.setPosition(100, 300 + progresses.size() * 60 + 30);
		entry.progress.setSize(1080, 28);
//		entry.message = box.createText2D(UIStyles.FONT);
//		entry.message.setPosition(100, 300 + progresses.size() * 60);
		progresses.push(entry);
		return entry;
	}
	
	public void pop() {
		ProgressEntry entry = progresses.pop();
//		box.remove(entry.message);
		box.remove(entry.progress);
	}

	public static final class ProgressEntry {
		public SimpleProgressBar progress;
		public Text2D message;
	}
}
