package com.github.nickid2018.chemistrylab.mod.imc;

import java.util.*;
import com.google.common.base.*;
import com.github.nickid2018.chemistrylab.util.*;

public class Conflicts {

	private static final List<ConflictManager<?>> MANAGERS = new ArrayList<>();
	private static boolean ordered = false;

	public static final ConflictManager<?> getManager(Class<?> clazz) {
		if (!ordered)
			MANAGERS.sort((c1, c2) -> c1.getConflictClass().getName().compareTo(c2.getConflictClass().getName()));
		int index = CollectionUtils.binarySearch(MANAGERS,
				manager -> manager.getConflictClass().getName().compareTo(clazz.getName()));
		Preconditions.checkArgument(index >= 0, "Can't find Conflict Manager of " + clazz);
		return MANAGERS.get(index);
	}
}
