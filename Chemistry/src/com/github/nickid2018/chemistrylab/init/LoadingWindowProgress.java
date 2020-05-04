package com.github.nickid2018.chemistrylab.init;

import java.util.*;

public class LoadingWindowProgress {

	private final Stack<ProgressEntry> progresses = new Stack<>();

	LoadingWindowProgress() {
		
	}

	public ProgressEntry push(int size) {
		ProgressEntry entry = new ProgressEntry();
//		entry.progress = new SimpleProgressBar(size, UIStyles.PROGRESSBAR_BACKGROUND, UIStyles.PROGRESSBAR_FOREGROUND);
//		entry.progress.setPosition(100, 430 + progresses.size() * 80 + 25);
//		entry.progress.setSize(1080, 28);
//		progresses.push(entry);
//		box.add(entry.message);
//		box.add(entry.progress);
		return entry;
	}

	public void pop() {
//		ProgressEntry entry = progresses.pop();
//		box.remove(entry.message);
//		box.remove(entry.progress);
	}

	public static final class ProgressEntry {
//		public ProgressBar progress;
//		public Label message;
	}
}
