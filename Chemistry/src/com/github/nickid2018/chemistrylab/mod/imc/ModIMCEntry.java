package com.github.nickid2018.chemistrylab.mod.imc;

public final class ModIMCEntry {

	public ConflictType conflictFunction = ConflictType.IGNORE;
	public SendChannel channel;
	public IConflictable thingToSend;

	public ModIMCEntry copy(ModIMCEntry entry) {
		conflictFunction = entry.conflictFunction;
		channel = entry.channel;
		thingToSend = entry.thingToSend;
		return this;
	}
}