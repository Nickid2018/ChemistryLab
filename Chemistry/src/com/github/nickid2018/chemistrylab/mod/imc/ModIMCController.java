package com.github.nickid2018.chemistrylab.mod.imc;

import java.util.*;
import java.util.concurrent.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.mod.*;

public class ModIMCController {

	private static final Map<SendChannel, Queue<ModIMCEntry>> imcEntries = new ConcurrentHashMap<>();

	public static final boolean sendTo(String from, String to, ConflictType type, IConflictable<?> thing) {
		ModIMCEntry findedEntry = new ModIMCEntry();
		SendChannel channel = new SendChannel(from, to);
		imcEntries.computeIfAbsent(channel.reverse(), k -> new ConcurrentLinkedQueue<>()).forEach(entry -> {
			if (entry.thingToSend.equals(thing))
				findedEntry.copy(entry);
		});
		if (findedEntry.conflictFunction.priority > type.priority)
			return false;
		if (findedEntry.conflictFunction.priority < type.priority)
			imcEntries.get(channel.reverse()).remove(findedEntry);
		ModIMCEntry entry = new ModIMCEntry();
		entry.conflictFunction = type;
		entry.channel = channel;
		entry.thingToSend = thing;
		imcEntries.computeIfAbsent(channel, k -> new ConcurrentLinkedQueue<>()).offer(entry);
		return true;
	}

	public static final void sendAll(String from, ConflictType type, IConflictable<?> thing) {
		for (ModContainer mod : ModController.MODS) {
			sendTo(from, mod.getModId(), type, thing);
		}
	}

	// True for dealing, false for sending
	public static boolean imcStage = false;

	public static SendChannel nowChannel;
	public static int process;
	public static int total;

	public static ConflictManager<?> nowDealing;

	public static final int getChannels() {
		return imcEntries.size();
	}

	public static final int getIMCSize() {
		int size = 0;
		for (Queue<ModIMCEntry> entrys : imcEntries.values()) {
			size += entrys.size();
		}
		return size;
	}

	@SuppressWarnings({ "unchecked" })
	public static final void imcProcess(LoadingWindowProgress progresses) {
		LoadingWindowProgress.ProgressEntry all = progresses.push(2);
		all.message.getInfo().setText("Send IMC Messages (1/2)");
		all.progress.setCurrent(1);
		LoadingWindowProgress.ProgressEntry detail = progresses.push(getChannels());
		// Re-order to find mod
		ModController.doBeforeIMCProcess();
		// Send conflicts
		Map<Class<? extends IConflictable<?>>, Set<ModIMCEntry>> messages = new HashMap<>();
		int index = 0;
		for (Map.Entry<SendChannel, Queue<ModIMCEntry>> en : imcEntries.entrySet()) {
			index++;
			Queue<ModIMCEntry> entrys = en.getValue();
			nowChannel = en.getKey();
			total = entrys.size();
			process = 0;
			detail.progress.setCurrent(index);
			while (!entrys.isEmpty()) {
				process++;
				detail.message.getInfo().setText(nowChannel + " (" + process + "/" + total + ")");
				ModIMCEntry entry = entrys.poll();
				// Add Conflict messages
				messages.computeIfAbsent((Class<? extends IConflictable<?>>) entry.thingToSend.getClass(),
						k -> new HashSet<>()).add(entry);
				ModController.findMod(entry.channel.to).sendIMCMessage(entry);
			}
		}
		// Send Conflicts to Manager
		imcStage = true;
		all.message.getInfo().setText("Dealing Conflicts (2/2)");
		all.progress.setCurrent(2);
		detail.progress.setMax(messages.size());
		detail.progress.setCurrent(0);
		int now = 0;
		for (ConflictManager<?> manager : Conflicts.getManagers()) {
			now++;
			detail.progress.setCurrent(now);
			nowDealing = manager;
			detail.message.getInfo().setText(nowDealing.getConflictName() + " (" + now + "/" + messages.size() + ")");
			Set<ModIMCEntry> entries = messages.get(manager.getConflictClass());
			nowDealing.dealConflict(entries == null ? Collections.emptySet() : entries);
			messages.remove(manager.getConflictClass());
		}
		progresses.pop();
		progresses.pop();
	}
}
