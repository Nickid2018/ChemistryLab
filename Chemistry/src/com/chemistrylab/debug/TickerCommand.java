package com.chemistrylab.debug;

import com.chemistrylab.eventbus.*;

public class TickerCommand extends Command {

	@Override
	public String invokeCommand(String info) throws CommandException {
		switch (info) {
		case "start":
			Ticker.startTick();
			return "Started ticker.";
		case "stop":
			Ticker.stopTick();
			return "Stopped ticker.";
		}
		throw new CommandException("Unknown Command");
	}

}
