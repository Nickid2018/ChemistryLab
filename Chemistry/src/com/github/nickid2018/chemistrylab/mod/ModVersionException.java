package com.github.nickid2018.chemistrylab.mod;

public class ModVersionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -744404164466605392L;

	private String mod;
	private String accepts;

	public ModVersionException(String mod, String accepts) {
		this.mod = mod;
		this.accepts = accepts;
	}

	@Override
	public String getMessage() {
		return "Mod " + mod + " cannot be run in this version. The mod can only be run in " + accepts;
	}
}
