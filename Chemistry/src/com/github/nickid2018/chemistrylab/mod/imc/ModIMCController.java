package com.github.nickid2018.chemistrylab.mod.imc;

import java.util.*;
import java.util.concurrent.*;
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

	@SuppressWarnings("unchecked")
	public static final void imcProcess() {
		// Re-order to find mod
		ModController.doBeforeIMCProcess();
		// Send conflicts
		Map<Class<? extends IConflictable<?>>, Set<ModIMCEntry>> messages = new HashMap<>();
		for (Map.Entry<SendChannel, Queue<ModIMCEntry>> en : imcEntries.entrySet()) {
			String sendto = en.getKey().to;
			Queue<ModIMCEntry> entrys = en.getValue();
			nowChannel = en.getKey();
			total = entrys.size();
			process = 0;
			while (!entrys.isEmpty()) {
				process++;
				ModIMCEntry entry = entrys.poll();
				// Add Conflict messages
				messages.computeIfAbsent((Class<? extends IConflictable<?>>) entry.thingToSend.getClass(),
						k -> new HashSet<>()).add(entry);
				ModController.findMod(sendto).sendIMCMessage(entry);
			}
		}
		// Send Conflicts to Manager
		imcStage = true;
		for (Map.Entry<Class<? extends IConflictable<?>>, Set<ModIMCEntry>> en : messages.entrySet()) {
			(nowDealing = Conflicts.getManager(en.getKey())).dealConflict(en.getValue());
		}
	}
}
