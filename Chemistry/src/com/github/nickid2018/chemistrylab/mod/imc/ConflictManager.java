package com.github.nickid2018.chemistrylab.mod.imc;

import java.util.*;

public abstract class ConflictManager<T extends IConflictable<T>> {

	public abstract Class<T> getConflictClass();

	public abstract void dealConflict(Set<ModIMCEntry> entries);
	
	public abstract String getConflictName();
}
