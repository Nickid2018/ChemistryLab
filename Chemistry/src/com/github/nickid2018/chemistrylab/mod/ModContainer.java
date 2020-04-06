package com.github.nickid2018.chemistrylab.mod;

import java.lang.reflect.*;
import com.github.mmc1234.mod.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.mod.event.ModLifeCycleEvent;
import com.github.nickid2018.chemistrylab.mod.event.ModPreInitEvent;
import com.github.nickid2018.chemistrylab.util.*;

public final class ModContainer {

	private Mod mod;
	private String modid;
	private Object modObject;
	private Class<?> modClass;
	private ModInstance instance;
	private AnnotationMap annotations;
	private ModState state = ModState.FOUND;
	private EnumModError error = EnumModError.NONE;

	ModContainer(ModInstance instance) {
		this.instance = instance;
		annotations = instance.getAnnotationMap();

		// Check Version
		AnnotationItem item = annotations.get(ModController.MOD_ANNOTATION);
		String range = "none";
		for (AnnotationAttrib attrib : item) {
			if (attrib.getKey().equals("acceptVersion")) {
				range = (String) attrib.getValue();
			}
			if (attrib.getKey().equals("modid")) {
				modid = (String) attrib.getValue();
			}
		}
		try {
			if (!range.equals("none") && !VersionUtils.isInRange(ChemistryLab.VERSION, range)) {
				onError(EnumModError.VERSION_MISMATCH, "The mod cannot run in this version.",
						new ModVersionException(modid, range));
				return;
			}
		} catch (Throwable t) {
			onError(EnumModError.MOD_CODE_ERROR, "Cannot check version, statement is wrong?", t);
			return;
		}

		// Load the class
		try {
			String clazz = instance.getModMain();
			clazz = clazz.substring(0, clazz.length() - 6);
			modClass = Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			onError(EnumModError.CLASS_LOAD_FAIL, "Cannot load the mod main class.", e);
			return;
		} catch (ExceptionInInitializerError e) {
			onError(EnumModError.CLASS_LOAD_FAIL,
					"Cannot load the mod because initialization is interrupted by exception.", e);
			return;
		}
		mod = modClass.getAnnotation(Mod.class);

		// Create instance
		try {
			modObject = modClass.newInstance();
		} catch (InstantiationException e) {
			onError(EnumModError.MOD_CODE_ERROR, "ModLoader cannot create instance of mod main class,"
					+ " please check the class has default null constructor.", e);
			return;
		} catch (IllegalAccessException e) {
			onError(EnumModError.INTERAL_ERROR, "ModLoader cannot create instance of mod main class,"
					+ " please check the class has a public default null constructor.", e);
			return;
		}

		// Fill instance
		Field[] fields = modClass.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Instance.class)) {
				field.setAccessible(true);
				try {
					field.set(instance, instance);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					onError(EnumModError.UNKNOWN_ERROR, "Set instance error! We cannot know what happens =QAQ=", e);
				}
			}
		}
	}

	public boolean isFailed() {
		return state == ModState.FAILED;
	}

	public String getModId() {
		return modid;
	}

	void setState(ModState state) {
		this.state = state;
	}

	public ModState getState() {
		return state;
	}

	void setErrorState(EnumModError state) {
		error = state;
	}

	public EnumModError getError() {
		return error;
	}

	public Class<?> getModMainClass() {
		return modClass;
	}

	public String getModFile() {
		return instance.getModFile().getModFilePath();
	}

	public Mod getMod() {
		return mod;
	}

	public void sendPreInit(TextureRegistry registry) {
		ModPreInitEvent event = new ModPreInitEvent(modid, registry);
		if (isFailed())
			return;
		state = ModState.PREINIT;
		sendEvent(event);
	}

	private void sendEvent(ModLifeCycleEvent event) {
		Method[] methods = modClass.getDeclaredMethods();
		for (Method method : methods) {
			Class<?>[] params = method.getParameterTypes();
			if (params.length == 1 && params[0].equals(event.getClass()))
				try {
					method.invoke(modObject, event);
				} catch (InvocationTargetException e) {
					onError(EnumModError.MOD_CODE_ERROR, "Error happens in mod code.", e.getTargetException());
					break;
				} catch (IllegalAccessException | IllegalArgumentException e) {
					onError(EnumModError.INTERAL_ERROR,
							"ModLoader cannot invoke the event method, is it non-access or static?", e);
					break;
				} catch (Exception e) {
					onError(EnumModError.UNKNOWN_ERROR, "We cannot know what happens =QAQ=", e);
					break;
				}
		}
	}

	private void onError(EnumModError error, String errorm, Throwable err) {
		ModController.logger.error("[Mod " + modid + "(File: " + getModFile() + " State: " + state + ")]Load Failed: "
				+ error + " Detail: " + errorm, err);
		state = ModState.FAILED;
		this.error = error;
	}
}
