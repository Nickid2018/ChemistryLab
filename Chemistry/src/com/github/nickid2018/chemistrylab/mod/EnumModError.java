package com.github.nickid2018.chemistrylab.mod;

public enum EnumModError {

	/**
	 * When the mod class cannot be found by {@linkplain Class#forName(String)} or
	 * create instance by {@linkplain Class#newInstance()}
	 */
	CLASS_LOAD_FAIL("The mod class cannot be found or be created."),
	/**
	 * When the mod cannot run in this version (Version Check)
	 */
	VERSION_MISMATCH("The mod cannot run in this version."),
	/**
	 * When the mod throws interal error
	 */
	MOD_CODE_ERROR("Error in mod codes."),
	/**
	 * When the error comes from Mod Loader
	 */
	INTERNAL_ERROR("Interal Error"),
	/**
	 * When the Mod Loader meets unknown error
	 */
	UNKNOWN_ERROR("Unknown Error"),
	/**
	 * When mod has no error
	 */
	NONE("");

	private final String message;

	EnumModError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
