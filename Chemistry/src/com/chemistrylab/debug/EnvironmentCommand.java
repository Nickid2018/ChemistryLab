package com.chemistrylab.debug;

import com.chemistrylab.reaction.*;

public class EnvironmentCommand extends Command {

	@Override
	public String invokeCommand(String info) throws CommandException {
		String[] div = info.split(" ",2);
		switch (div[0]) {
		case "temperature":
			Environment.setTemperature(Double.parseDouble(div[1]));
			return "Changed temperature to "+div[1]+"K";
		case "pressure":
			Environment.setPressure(Double.parseDouble(div[1]));
			return "Changed pressure to "+div[1]+"Pa";
		}
		throw new CommandException("Unknown Command");
	}

}
