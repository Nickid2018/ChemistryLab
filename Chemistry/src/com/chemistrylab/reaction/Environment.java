package com.chemistrylab.reaction;

import java.io.*;
import java.util.*;
import com.chemistrylab.properties.*;

public final class Environment {
	
	private static Map<String,Property<?>> settings;
	
	public static final void init() throws Exception{
		Properties pro=new Properties();
		InputStream is=Environment.class.getResourceAsStream("/assets/models/environment.properties");
		pro.load(is);
		settings=Property.getProperties(pro, (PropertyReader)name->{
			switch (name) {
			case "temperature":
			case "pressure":
				return new DoubleProperty();
			default:
				throw new RuntimeException("Unrecognize tag for "+name);
			}
		});
	}
	
	public static final double getTemperature(){
		return (double) settings.get("temperature").getValue();
	}
	
	public static final double getPressure(){
		return (double) settings.get("pressure").getValue();
	}
	
	public static final void setTemperature(double t){
		DoubleProperty p=new DoubleProperty();
		p.setValue(t);
		settings.replace("temperature", p);
	}
	
	public static final void setPressure(double t){
		DoubleProperty p=new DoubleProperty();
		p.setValue(t);
		settings.replace("pressure", p);
	}
}
