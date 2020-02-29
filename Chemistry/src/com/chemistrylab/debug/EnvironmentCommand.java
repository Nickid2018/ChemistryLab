package com.chemistrylab.debug;

import com.chemistrylab.util.*;
import com.chemistrylab.reaction.*;

public class EnvironmentCommand extends Command {

	@Override
	public Message[] invokeCommand(String info) throws CommandException {
		String[] div = info.split(" ", 2);
		switch (div[0]) {
		case "temperature":
			// Format: environment temperature {value}
			Environment.setTemperature(Double.parseDouble(div[1]));
			return new Message[] { new Message().addMessageEntry(
					new MessageEntry(String.format(I18N.getString("command.environment.temp"), div[1]))) };
		case "pressure":
			// Format: environment pressure {value}
			Environment.setPressure(Double.parseDouble(div[1]));
			return new Message[] { new Message().addMessageEntry(
					new MessageEntry(String.format(I18N.getString("command.environment.press"), div[1]))) };
		}
		throw new CommandException(I18N.getString("command.unknown"));
	}

}
