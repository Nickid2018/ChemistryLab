package com.github.nickid2018.chemistrylab.mod;

public enum ModState {

	/**
	 * A mod has been found by ModLoader
	 */
	FOUND("Mod %s is found.", "S"),
	/**
	 * A mod has run at preinit stage
	 */
	PREINIT("Mod %s is pre-initializing.", "P"),
	/**
	 * A mod has run at init stage
	 */
	INIT("Mod %s is initializing.", "I"),
	/**
	 * A mod is communicating with other mods
	 */
	IMC_STATE("Mod %s is doing inter-mods communication.", "C"),
	/**
	 * A mod has run at postinit stage
	 */
	POSTINIT("Mod %s is post-initializing.", "T"),
	/**
	 * A mod has been successfully loaded
	 */
	LOADED("Mod %s is loaded.", "L"),
	// Above are a mod normal lifecycle
	/**
	 * A mod can't be loaded
	 */
	FAILED("Mod %s loading failed.", "F");

	private final String message;
	private final String abbr;

	ModState(String message, String abbr) {
		this.message = message;
		this.abbr = abbr;
	}

	public String formatState(String modid) {
		return String.format(message, modid);
	}

	public String getAbbr() {
		return abbr;
	}
}
