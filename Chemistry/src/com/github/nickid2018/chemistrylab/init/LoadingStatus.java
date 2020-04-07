package com.github.nickid2018.chemistrylab.init;

public enum LoadingStatus {
	/**
	 * Before mod finding
	 */
	START("Preparing Initialization"),
	/**
	 * Pre-Initialization (For mods)
	 */
	PRE_INIT("Mod PreInitialization"),
	/**
	 * Load Textures!
	 */
	INIT_TEXTURE("Initialization - Load Textures"),
	/**
	 * Initialization (For mods)
	 */
	INIT_MOD("Initialization - Mod Initialization"),
	/**
	 * Contains three statuses: CHEMICAL_LOAD, CONTAINER_LOAD, REACTION_LOAD
	 */
	INIT_CHEMISTRY("Initialization - Chemistry Engine"),
	/**
	 * Inter-Mod Communication Message Enqueue
	 */
	IMC_ENQUEUE("PostInitialization - IMC Enqueue"),
	/**
	 * Post-Initialization (For mods)
	 */
	POST_INIT("Mod PostInitialization"),
	/**
	 * Inter-Mod Communication Message Send
	 */
	IMC_PROCESS("PostInitialization - IMC Process"),
	/**
	 * Finishing Initialization (For many thing push-stacks)
	 */
	FINISHING("Finishing ChemistryLab Initialization");

	public String status;

	private LoadingStatus(String status) {
		this.status = status;
	}
}
