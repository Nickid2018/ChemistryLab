package com.github.nickid2018.chemistrylab.reaction;

import java.io.*;
import java.util.*;
import com.github.nickid2018.jmcl.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.event.*;
import com.github.nickid2018.chemistrylab.resource.*;
import com.github.nickid2018.chemistrylab.container.*;
import com.github.nickid2018.chemistrylab.properties.*;

public final class Environment {

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
		EnvironmentEvent ev = new EnvironmentEvent();
		ev.changedItem = "temperature";
		ev.oldValue = settings.replace("temperature", p);
		AbstractContainer.CHEMICAL_BUS.post(ev);
		EnvironmentEvent ev2 = new EnvironmentEvent();
		ev.changedItem = "gasmolv";
		ev.oldValue = new DoubleProperty().setValue(getGasMolV());
		MathStatementProperty mp = (MathStatementProperty) settings.get("gasmolv");
		mp.setValue("T", getTemperature());
		mp.setValue("P", getPressure());
		mp.calc();
		AbstractContainer.CHEMICAL_BUS.post(ev2);
	}

	public static final void setPressure(double t) {
		DoubleProperty p = new DoubleProperty();
		p.setValue(t);
		EnvironmentEvent ev = new EnvironmentEvent();
		ev.changedItem = "pressure";
		ev.oldValue = settings.replace("pressure", p);
		AbstractContainer.CHEMICAL_BUS.post(ev);
		EnvironmentEvent ev2 = new EnvironmentEvent();
		ev.changedItem = "gasmolv";
		ev.oldValue = new DoubleProperty().setValue(getGasMolV());
		MathStatementProperty mp = (MathStatementProperty) settings.get("gasmolv");
		mp.setValue("T", getTemperature());
		mp.setValue("P", getPressure());
		mp.calc();
		AbstractContainer.CHEMICAL_BUS.post(ev2);
	}

	public static final void setSpeed(double speed) {
		DoubleProperty p = new DoubleProperty();
		p.setValue(speed);
		EnvironmentEvent ev = new EnvironmentEvent();
		ev.changedItem = "speed";
		ev.oldValue = settings.replace("speed", p);
		AbstractContainer.CHEMICAL_BUS.post(ev);
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
