package com.github.nickid2018.chemistrylab.exception;

import java.io.*;
import java.util.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.apache.commons.io.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.sound.*;

public class ErrorUtils {

	// Print Throwable as String
	public static String asStack(Throwable e) {
		String l = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder(e.toString().replace("\n", l) + l);
		StackTraceElement[] sks = e.getStackTrace();
		for (StackTraceElement ste : sks) {
			sb.append("\tat " + ste + l);
		}
		Throwable t = e;
		while ((t = t.getCause()) != null) {
			sb.append("Caused by:" + t + l);
			StackTraceElement[] sks0 = t.getStackTrace();
			int i = 0;
			for (StackTraceElement ste : sks0) {
				StackTraceElement ate = null;
				try {
					ate = sks[sks.length - (sks0.length - i)];
				} catch (Exception e2) {
				}
				if (ste.equals(ate)) {
					sb.append("\t... " + (sks0.length - i) + " more" + l);
					break;
				}
				sb.append("\tat " + ste + l);
				i++;
			}
		}
		Throwable[] ss = e.getSuppressed();
		for (Throwable s : ss) {
			sb.append("Suppressed:" + s + l);
			StackTraceElement[] sks0 = s.getStackTrace();
			int i = 0;
			for (StackTraceElement ste : sks0) {
				StackTraceElement ate = null;
				try {
					ate = sks[sks.length - (sks0.length - i)];
				} catch (Exception e2) {
				}
				if (ste.equals(ate)) {
					sb.append("\t... " + (sks0.length - i) + " more" + "l");
					break;
				}
				sb.append("\tat " + ste + l);
			}
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	// Stack as StackTraceElement
	public static String asStack(StackTraceElement[] es) {
		String l = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement ste : es) {
			sb.append("\tat " + ste + l);
		}
		return sb.toString();
	}

	public static String outputCrashLog(Thread errorthread, Throwable e) {
		LayerRender.popLayers();
		if (errorthread == null)
			errorthread = Thread.currentThread();
		Date date = new Date();
		String crash = "crash-report_"
				+ String.format("%tY%tm%td%tH%tM%tS%tL", date, date, date, date, date, date, date) + ".csh.log";
		String l = System.getProperty("line.separator");
		String stack = asStack(e);
		// Write crash log
		File crashrep = new File("crash-reports/" + crash);
		crash = crashrep.getAbsolutePath();
		FileWriter w;
		try {
			crashrep.createNewFile();
			w = new FileWriter(crashrep);
			IOUtils.write("Program had crashed.This report is the detail of this error." + l, w);
			IOUtils.write("Time " + String.format("%tc", date) + l, w);
			IOUtils.write("=== S T A C K T R A C E ===" + l, w);
			IOUtils.write("Thread \"" + errorthread.getName() + "\"" + l, w);
			IOUtils.write(stack + l, w);
			IOUtils.write("=== T H R E A D S ===" + l, w);
			for (Map.Entry<Thread, StackTraceElement[]> en : Thread.getAllStackTraces().entrySet()) {
				IOUtils.write("Thread \"" + en.getKey().getName() + "\" State:" + en.getKey().getState() + l, w);
				IOUtils.write(asStack(en.getValue()) + l, w);
			}
			IOUtils.write("=== S Y S T E M ===" + l, w);
			IOUtils.write("Operating System:" + System.getProperty("os.name") + " " + System.getProperty("os.version")
					+ " " + System.getProperty("os.arch") + l, w);
			IOUtils.write(
					"Java:" + System.getProperty("java.version") + "\tPath:" + System.getProperty("java.home") + l, w);
			IOUtils.write("Library Path: " + System.getProperty("java.library.path").replaceAll(";", l) + l, w);
			IOUtils.write("LWJGL Version: " + Version.getVersion() + l, w);
			IOUtils.write("GLFW Version: " + GLFW.glfwGetVersionString() + l, w);
			IOUtils.write("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION) + l, w);
			IOUtils.write("OpenAL Version: " + SoundSystem.getALVersion() + " ALC " + SoundSystem.getALCVersion() + l,
					w);
			w.flush();
			w.close();
		} catch (IOException e2) {
			EngineChemistryLab.logger.error("Write crash-report error.", e2);
		}
		return crash;
	}

}
