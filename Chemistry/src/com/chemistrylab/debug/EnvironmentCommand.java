package com.chemistrylab.debug;

import com.chemistrylab.util.*;
import com.chemistrylab.reaction.*;

public class EnvironmentCommand extends Command {

	@Override
	public Message[] invokeCommand(String info) throws CommandException {
		String[] div = info.split(" ", 2);
		switch (div[0]) {
		case "temperature":
			Environment.setTemperature(Double.parseDouble(div[1]));
			return new Message[] {
					new Message().addMessageEntry(new MessageEntry("Changed temperature to " + div[1] + "K")) };
		case "pressure":
			Environment.setPressure(Double.parseDouble(div[1]));
			return new Message[] {
					new Message().addMessageEntry(new MessageEntry("Changed pressure to " + div[1] + "Pa")) };
		}
		throw new CommandException("Unknown Command");
	}

}
