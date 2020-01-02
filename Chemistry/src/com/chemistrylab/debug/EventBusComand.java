package com.chemistrylab.debug;

import com.chemistrylab.eventbus.*;

public class EventBusComand extends Command {

	@Override
	public String invokeCommand(String info) throws CommandException {
		switch (info) {
		case "nonwait-size":
			return "EventBus Nonwait Size:" + EventBus.getNonawaitSize();
		case "await-units":
			return "EventBus Await Unit Size:" + EventBus.getAvailableAwaitUnits();
		}
		throw new CommandException("Unknown Command");
	}

}
