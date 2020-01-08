package com.chemistrylab.reaction;

import java.io.*;
import java.util.*;
import com.cj.jmcl.*;
import com.chemistrylab.init.*;
import com.chemistrylab.eventbus.*;
import com.chemistrylab.properties.*;

public final class Environment {
	
	public static final class EventEnvironmentChanged extends Event {
		
		private String changedItem;
		private Property<?> oldValue;

		public String getChangedItem() {
			return changedItem;
		}

		public Property<?> getOldValue() {
			return oldValue;
		}

		@Override
		public Event clone() {
			EventEnvironmentChanged ret = new EventEnvironmentChanged();
			ret.eventId = eventId;
			return ret;
		}
	}

	public static final Event ENVIRONMENT_CHANGED = new EventEnvironmentChanged();
	
	private static Map<String,Property<?>> settings;
	
	public static final void init() throws Exception{
		Properties pro=new Properties();
		InputStream is=Environment.class.getResourceAsStream("/assets/models/environment.properties");
		pro.load(is);
		JMCLRegister.registerVariable("T");
		JMCLRegister.registerVariable("P");
		settings=Property.getProperties(pro, (PropertyReader)name->{
			switch (name) {
			case "temperature":
			case "pressure":
				return new DoubleProperty();
			case "gasmolv":
				return new MathStatementProperty("T","P");
			default:
				throw new RuntimeException("Unrecognize tag for "+name);
			}
		});
		MathStatementProperty p = (MathStatementProperty) settings.get("gasmolv");
		p.setValue("T", getTemperature());
		p.setValue("P", getPressure());
		p.calc();
	}
	
	public static final double getTemperature(){
		return (double) settings.get("temperature").getValue();
	}
	
	public static final double getPressure(){
		return (double) settings.get("pressure").getValue();
	}
	
	public static final double getGasMolV(){
		MathStatementProperty p = (MathStatementProperty) settings.get("gasmolv");
		return MathHelper.eplison(p.getValue());
	}
	
	public static final void setTemperature(double t){
		DoubleProperty p=new DoubleProperty();
		p.setValue(t);
		EventEnvironmentChanged ev = (EventEnvironmentChanged) ENVIRONMENT_CHANGED.clone();
		ev.changedItem = "temperature";
		ev.oldValue = settings.replace("temperature", p);
		EventBus.postEvent(ev);
		EventEnvironmentChanged ev2 = (EventEnvironmentChanged) ENVIRONMENT_CHANGED.clone();
		ev.changedItem = "gasmolv";
		ev.oldValue = new DoubleProperty().setValue(getGasMolV());
		MathStatementProperty mp = (MathStatementProperty) settings.get("gasmolv");
		mp.setValue("T", getTemperature());
		mp.setValue("P", getPressure());
		mp.calc();
		EventBus.postEvent(ev2);
	}
	
	public static final void setPressure(double t){
		DoubleProperty p=new DoubleProperty();
		p.setValue(t);
		EventEnvironmentChanged ev = (EventEnvironmentChanged) ENVIRONMENT_CHANGED.clone();
		ev.changedItem = "pressure";
		ev.oldValue = settings.replace("pressure", p);
		EventBus.postEvent(ev);
		EventEnvironmentChanged ev2 = (EventEnvironmentChanged) ENVIRONMENT_CHANGED.clone();
		ev.changedItem = "gasmolv";
		ev.oldValue = new DoubleProperty().setValue(getGasMolV());
		MathStatementProperty mp = (MathStatementProperty) settings.get("gasmolv");
		mp.setValue("T", getTemperature());
		mp.setValue("P", getPressure());
		mp.calc();
		EventBus.postEvent(ev2);
	}
}
