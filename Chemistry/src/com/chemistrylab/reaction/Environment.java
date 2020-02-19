package com.chemistrylab.reaction;

import java.io.*;
import java.util.*;
import com.cj.jmcl.*;
import com.chemistrylab.init.*;
import com.chemistrylab.util.*;
import com.chemistrylab.eventbus.*;
import com.chemistrylab.properties.*;

public final class Environment {

	public static final Event ENVIRONMENT_CHANGED = Event.createNewEvent("Environment_Changed");

	public static final int ENVIRONMENT_CHANGE_ITEM = 0;
	public static final int ENVIRONMENT_OLD_VALUE = 1;

	private static Map<String, Property<?>> settings;

	public static final void init() throws Exception {
		Properties pro = new Properties();
		InputStream is = ResourceManager.getResourceAsStream("config/environment.properties", true);
		pro.load(is);
		JMCLRegister.registerVariable("T");
		JMCLRegister.registerVariable("P");
		settings = Property.getProperties(pro, (PropertyReader) name -> {
			switch (name) {
			case "temperature":
			case "pressure":
				return new DoubleProperty();
			case "gasmolv":
				return new MathStatementProperty("T", "P");
			case "speed":
				return new DoubleProperty();
			default:
				throw new RuntimeException("Unrecognize tag for " + name);
			}
		});
		MathStatementProperty p = (MathStatementProperty) settings.get("gasmolv");
		p.setValue("T", getTemperature());
		p.setValue("P", getPressure());
		p.calc();
	}

	public static final double getTemperature() {
		return (double) settings.get("temperature").getValue();
	}

	public static final double getPressure() {
		return (double) settings.get("pressure").getValue();
	}

	public static final double getGasMolV() {
		MathStatementProperty p = (MathStatementProperty) settings.get("gasmolv");
		return MathHelper.eplison(p.getValue());
	}

	public static final double getSpeed() {
		return (double) settings.get("speed").getValue();
	}

	public static final void setTemperature(double t) {
		DoubleProperty p = new DoubleProperty();
		p.setValue(t);
		Event ev = ENVIRONMENT_CHANGED.clone();
		ev.putExtra(ENVIRONMENT_CHANGE_ITEM, "temperature");
		ev.putExtra(ENVIRONMENT_OLD_VALUE, settings.replace("temperature", p));
		EventBus.postEvent(ev);
		Event ev2 = ENVIRONMENT_CHANGED.clone();
		ev.putExtra(ENVIRONMENT_CHANGE_ITEM, "gasmolv");
		ev.putExtra(ENVIRONMENT_OLD_VALUE, new DoubleProperty().setValue(getGasMolV()));
		MathStatementProperty mp = (MathStatementProperty) settings.get("gasmolv");
		mp.setValue("T", getTemperature());
		mp.setValue("P", getPressure());
		mp.calc();
		EventBus.postEvent(ev2);
	}

	public static final void setPressure(double t) {
		DoubleProperty p = new DoubleProperty();
		p.setValue(t);
		Event ev = ENVIRONMENT_CHANGED.clone();
		ev.putExtra(ENVIRONMENT_CHANGE_ITEM, "pressure");
		ev.putExtra(ENVIRONMENT_OLD_VALUE, settings.replace("pressure", p));
		EventBus.postEvent(ev);
		Event ev2 = ENVIRONMENT_CHANGED.clone();
		ev.putExtra(ENVIRONMENT_CHANGE_ITEM, "gasmolv");
		ev.putExtra(ENVIRONMENT_OLD_VALUE, new DoubleProperty().setValue(getGasMolV()));
		MathStatementProperty mp = (MathStatementProperty) settings.get("gasmolv");
		mp.setValue("T", getTemperature());
		mp.setValue("P", getPressure());
		mp.calc();
		EventBus.postEvent(ev2);
	}

	public static final void setSpeed(double speed) {
		DoubleProperty p = new DoubleProperty();
		p.setValue(speed);
		Event ev = ENVIRONMENT_CHANGED.clone();
		ev.putExtra(ENVIRONMENT_CHANGE_ITEM, "speed");
		ev.putExtra(ENVIRONMENT_OLD_VALUE, settings.replace("speed", p));
		EventBus.postEvent(ev);
	}

	public static final void saveSettings() throws IOException {
		Properties pro = new Properties();
		pro.put("temperature", getTemperature() + "");
		pro.put("pressure", getPressure() + "");
		pro.put("gasmolv", settings.get("gasmolv").toString());
		pro.put("speed", getSpeed() + "");
		Writer w = new OutputStreamWriter(ResourceManager.getOutputStream("config/environment.properties"));
		pro.store(w, "Environment Settings");
	}
}
