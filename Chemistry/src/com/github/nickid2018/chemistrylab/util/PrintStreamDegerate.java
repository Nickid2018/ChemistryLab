package com.github.nickid2018.chemistrylab.util;

import java.io.*;
import javax.annotation.*;
import org.apache.logging.log4j.*;

public class PrintStreamDegerate extends PrintStream{
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	private String name;

	public PrintStreamDegerate(String name, OutputStream out) {
		super(out);
		this.name = name;
	}

	public void println(@Nullable String string) {
		log(string);
	}

	public void println(Object object) {
		log(String.valueOf(object));
	}
	
	protected void log(@Nullable String string) {
		LOGGER.info("[{}]: {}", this.name, string);
	}
}
