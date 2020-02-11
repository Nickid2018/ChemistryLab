package com.chemistrylab.debug;

import com.chemistrylab.util.*;
import com.chemistrylab.eventbus.*;

public class TickerCommand extends Command {

	@Override
	public Message[] invokeCommand(String info) throws CommandException {
		switch (info) {
		case "start":
			Ticker.startTick();
			return new Message[] { new Message().addMessageEntry(new MessageEntry("Started ticker.")) };
		case "stop":
			Ticker.stopTick();
			return new Message[] { new Message().addMessageEntry(new MessageEntry("Stopped ticker.")) };
		case "reset":
			Ticker.stopTick();
			Ticker.startTick();
			return new Message[] { new Message().addMessageEntry(new MessageEntry("Reset ticker.")) };
		}
		throw new CommandException("Unknown Command");
	}

}
