package com.github.nickid2018.chemistrylab.mod.imc;

import java.util.*;

public class Conflicts {

	private static final Map<Class<?>, ConflictManager<?>> MANAGERS = new HashMap<>();
	
	public static final Collection<ConflictManager<?>> getManagers(){
		return MANAGERS.values();
	}

	@SuppressWarnings("unchecked")
	public static final <T extends IConflictable<T>> ConflictManager<T> getManager(Class<T> clazz) {
		return (ConflictManager<T>) MANAGERS.get(clazz);
	}

	public static final <T extends IConflictable<T>> void putConflictManager(ConflictManager<T> manager) {
		MANAGERS.put(manager.getConflictClass(), manager);
	}
}
