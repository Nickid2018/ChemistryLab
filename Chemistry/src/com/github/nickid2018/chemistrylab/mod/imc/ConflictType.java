package com.github.nickid2018.chemistrylab.mod.imc;

public enum ConflictType {
	/**
	 * Merge types/chemicals if possible.
	 */
	MERGE(0),
	/**
	 * Override the types/chemicals by the types/chemicals whose priority is higher.
	 */
	OVERRIDE(1),
	/**
	 * Ignore conflicts, which means will create two different same-name things.
	 */
	IGNORE(-1);

	public final int priority;

	ConflictType(int priority) {
		this.priority = priority;
	}
}