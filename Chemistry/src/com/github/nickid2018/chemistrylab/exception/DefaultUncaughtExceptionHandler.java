package com.github.nickid2018.chemistrylab.exception;

import javax.swing.*;
import java.lang.Thread.*;

public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.gc();
		// Use AWT
		onError(e);
	}

	public static void onError(Throwable t) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		String stack = ErrorUtils.asStack(t);
		String crash = ErrorUtils.outputCrashLog(null, t);
		UIManager.getLookAndFeel().provideErrorFeedback(null);
		JOptionPane.showMessageDialog(null, "An error occurred!\n" + stack.replaceAll("\t", "    ")
				+ "\nThe crash report has been saved in " + crash, "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}

}
