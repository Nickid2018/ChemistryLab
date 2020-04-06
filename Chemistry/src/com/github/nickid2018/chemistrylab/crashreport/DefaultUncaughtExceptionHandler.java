package com.github.nickid2018.chemistrylab.crashreport;

import javax.swing.*;

import java.io.IOException;
import java.lang.Thread.*;
import com.github.nickid2018.chemistrylab.*;

public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.gc();
		if (ChemistryLab.INITED) {

		} else {
			// Use AWT
			onError(t, e);
		}
	}

	public static void onError(Thread thread, Throwable error) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		String crash;
		try {
			crash = new CrashReport(error, thread, "").writeCrash();
		} catch (IOException e) {
			crash = "/* Write Error! */";
		}
		UIManager.getLookAndFeel().provideErrorFeedback(null);
		new ErrorSwingWindow(error, crash).setVisible(true);
		System.exit(-1);
	}

}
