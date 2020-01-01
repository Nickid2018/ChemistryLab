package com.chemistrylab.reaction;

import java.io.*;
import java.util.*;
import com.cj.jmcl.*;
import com.chemistrylab.init.MathHelper;
import com.chemistrylab.properties.*;

public final class Environment {
	
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
		settings.replace("temperature", p);
	}
	
	public static final void setPressure(double t){
		DoubleProperty p=new DoubleProperty();
		p.setValue(t);
		settings.replace("pressure", p);
	}
}
