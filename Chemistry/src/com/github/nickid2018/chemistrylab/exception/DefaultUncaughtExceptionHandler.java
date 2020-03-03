package com.github.nickid2018.chemistrylab.exception;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.io.IOUtils;
import org.lwjgl.Version;

import com.github.nickid2018.chemistrylab.ChemistryLab;
import com.github.nickid2018.chemistrylab.eventbus.Event;
import com.github.nickid2018.chemistrylab.eventbus.EventBus;
import com.github.nickid2018.chemistrylab.window.Window;

public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.gc();
		if (Window.inited) {
			Event error = ChemistryLab.THREAD_FATAL.clone();
			error.putExtra(0, e);
			error.putExtra(1, t);
			EventBus.postEvent(error);
		} else {
			// Use AWT
			onError(e);
		}
	}
	public static void onError(Throwable t) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		Map<Event.CompleteComparedEvent, Integer> evsnap = EventBus.getNowActiveEvents();
		Date date = new Date();
		String crash = "crash-report_"
				+ String.format("%tY%tm%td%tH%tM%tS%tL", date, date, date, date, date, date, date) + ".csh.log";
		String l = System.getProperty("line.separator");
		String stack = ChemistryLab.asStack(t);
		File crashrep = new File("crash-reports/" + crash);
		crash = crashrep.getAbsolutePath();
		FileWriter w;
		try {
			crashrep.createNewFile();
			w = new FileWriter(crashrep);
			IOUtils.write("Program had crashed.This report is the detail of this error." + l, w);
			IOUtils.write("Time " + String.format("%tc", date) + l, w);
			IOUtils.write("=== S T A C K T R A C E ===" + l, w);
			IOUtils.write("Thread \"Render Thread\"" + l, w);
			IOUtils.write(stack + l, w);
			IOUtils.write("=== T H R E A D S ===" + l, w);
			for (Map.Entry<Thread, StackTraceElement[]> en : Thread.getAllStackTraces().entrySet()) {
				IOUtils.write("Thread \"" + en.getKey().getName() + "\" State:" + en.getKey().getState() + l, w);
				IOUtils.write(ChemistryLab.asStack(en.getValue()) + l, w);
			}
			IOUtils.write("=== E V E N T B U S ===" + l, w);
			if (Window.inited) {
				IOUtils.write("Active Events:" + l, w);
				for (Map.Entry<Event.CompleteComparedEvent, Integer> en : evsnap.entrySet()) {
					IOUtils.write(en.getKey() + " " + en.getValue() + l, w);
				}
			} else {
				IOUtils.write("EventBus hasn't been initialized." + l, w);
			}
			IOUtils.write("=== S Y S T E M ===" + l, w);
			IOUtils.write("Operating System:" + System.getProperty("os.name") + " " + System.getProperty("os.version")
					+ " " + System.getProperty("os.arch") + l, w);
			IOUtils.write(
					"Java:" + System.getProperty("java.version") + "\tPath:" + System.getProperty("java.home") + l, w);
			IOUtils.write("Library Path:" + System.getProperty("java.library.path").replaceAll(";", l) + l, w);
			IOUtils.write("LWJGL Version:" + Version.getVersion() + l, w);
			IOUtils.write("GLFW Version: Haven't been loaded" + l, w);
			IOUtils.write("OpenGL Version: Haven't been loaded" + l, w);
			IOUtils.write("OpenAL Version: Haven't been loaded" + l, w);
			w.flush();
			w.close();
		} catch (IOException e2) {
			ChemistryLab.logger.error("Write crash-report error.", e2);
		}
		UIManager.getLookAndFeel().provideErrorFeedback(null);
		JOptionPane.showMessageDialog(null, "An error occurred!\n" + stack.replaceAll("\t", "    ")
				+ "\nThe crash report has been saved in " + crash, "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}

}
