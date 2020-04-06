package com.github.nickid2018.chemistrylab.crashreport;

import java.io.*;
import java.util.*;
import org.hyperic.sigar.*;
import java.lang.management.*;
import org.apache.commons.io.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.mod.*;

public class CrashReport {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public final Throwable error;
	public final Thread thread;
	public final String message;

	public CrashReport(Throwable error, Thread thread, String message) {
		this.error = error;
		this.thread = thread == null ? Thread.currentThread() : thread;
		this.message = message;
	}

	public String writeCrash() throws IOException {
		File file = makeCrashFile();
		Writer writer = new FileWriter(file);
		IOUtils.write("Program had crashed.This report is the detail of this error." + LINE_SEPARATOR, writer);
		outputTime(writer);
		IOUtils.write(message + LINE_SEPARATOR, writer);
		outputStackTrace(writer);
		outputThreadDumps(writer);
		if (Boolean.valueOf(ProgramOptions.getCommandSwitch("-modEnable", "true")))
			outputModInfos(writer);
		outputVMRuntime(writer);
		outputSystemInfos(writer);
		writer.flush();
		writer.close();
		return file.getAbsolutePath();
	}

	public static final File makeCrashFile() throws IOException {
		File file = new File(
				String.format("crash-reports/crash-report_%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL.csh.log", new Date()));
		if (!file.exists())
			file.createNewFile();
		return file;
	}

	public static final void outputTime(Writer writer) throws IOException {
		IOUtils.write(String.format("Time %tc" + LINE_SEPARATOR, new Date()), writer);
	}

	public final void outputStackTrace(Writer writer) throws IOException {
		IOUtils.write("=== S T A C K T R A C E ===" + LINE_SEPARATOR, writer);
		IOUtils.write("Error in Thread \"" + thread.getName() + "\"" + LINE_SEPARATOR, writer);
		IOUtils.write(ErrorUtils.asStack(error) + LINE_SEPARATOR, writer);
	}

	public static final void outputThreadDumps(Writer writer) throws IOException {
		IOUtils.write("=== T H R E A D S ===" + LINE_SEPARATOR, writer);
		for (Map.Entry<Thread, StackTraceElement[]> en : Thread.getAllStackTraces().entrySet()) {
			IOUtils.write("Thread \"" + en.getKey().getName() + "\" State:" + en.getKey().getState() + LINE_SEPARATOR,
					writer);
			IOUtils.write(ErrorUtils.asStack(en.getValue()) + LINE_SEPARATOR, writer);
		}
	}

	public static final void outputModInfos(Writer writer) throws IOException {
		IOUtils.write("=== M O D S ===" + LINE_SEPARATOR, writer);
		for (ModContainer container : ModController.MODS.values()) {
			IOUtils.write("modid: " + container.getModId() + "(File: \"" + container.getModFile() + "\") State: "
					+ container.getState() + " Error: " + container.getError() + LINE_SEPARATOR, writer);
		}
	}

	public static final void outputVMRuntime(Writer writer) throws IOException {
		IOUtils.write("=== R U N T I M E ===" + LINE_SEPARATOR, writer);
		// VM Args
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		IOUtils.write("Arguments: " + runtime.getInputArguments() + LINE_SEPARATOR, writer);
		IOUtils.write("Boot Class Path: " + runtime.getBootClassPath().replaceAll(";", LINE_SEPARATOR) + LINE_SEPARATOR,
				writer);
		IOUtils.write("Class Path: " + runtime.getClassPath().replaceAll(";", LINE_SEPARATOR) + LINE_SEPARATOR, writer);
		IOUtils.write("Library Path: " + runtime.getLibraryPath().replaceAll(";", LINE_SEPARATOR) + LINE_SEPARATOR,
				writer);
		// Memory Details
		IOUtils.write(
				"Memory Heap: " + MathHelper.eplison(ChemistryLab.APPLICATION.getJavaHeap() / 1048576.0f, 1) + "MB/"
						+ MathHelper.eplison(ChemistryLab.RUNTIME.maxMemory() / 1048576.0f, 1) + "MB" + LINE_SEPARATOR,
				writer);
		IOUtils.write("Memory Details:" + LINE_SEPARATOR, writer);
		List<MemoryPoolMXBean> memory = ManagementFactory.getMemoryPoolMXBeans();
		for (MemoryPoolMXBean bean : memory) {
			IOUtils.write(
					"\t" + bean.getName() + "(" + bean.getType() + "):"
							+ MathHelper.eplison(bean.getUsage().getUsed() / 1024.0f, 1) + "KB out of "
							+ MathHelper.eplison(bean.getUsage().getMax() / 1024.0f, 1) + "KB" + LINE_SEPARATOR,
					writer);
		}
	}

	public static final void outputSystemInfos(Writer writer) throws IOException {
		// OS Infos
		IOUtils.write("=== S Y S T E M ===" + LINE_SEPARATOR, writer);
		IOUtils.write("Operation System: " + System.getProperty("os.name") + " " + System.getProperty("os.arch")
				+ LINE_SEPARATOR, writer);
		IOUtils.write("Java Path: " + System.getProperty("java.version") + " at " + System.getProperty("java.home")
				+ LINE_SEPARATOR, writer);
		try {
			Sigar sigar = new Sigar();
			CpuInfo[] infos = sigar.getCpuInfoList();
			StringBuilder builder = new StringBuilder();
			for (CpuInfo info : infos) {
				builder.append("CPU: " + info + LINE_SEPARATOR);
			}
		} catch (Throwable e) {
			IOUtils.write("// Cannot get CPU information" + LINE_SEPARATOR, writer);
		}
	}
}
