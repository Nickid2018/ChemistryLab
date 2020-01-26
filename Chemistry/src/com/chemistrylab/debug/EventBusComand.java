package com.chemistrylab.debug;

import java.util.*;
import com.alibaba.fastjson.*;
import com.chemistrylab.eventbus.*;

public class EventBusComand extends Command {

	@Override
	public String invokeCommand(String info) throws CommandException {
		switch (info) {
		case "nonwait-size":
			return "EventBus Nonwait Size:" + EventBus.getNonawaitSize();
		case "nonwait-pass":
			return "EventBus Nonwait Passed Size:" + EventBus.getPassedNonwaitEvents();
		case "await-units":
			return "EventBus Await Unit Size:" + EventBus.getAvailableAwaitUnits();
		case "reg-events":
			StringBuilder sb = new StringBuilder("EventBus Registered Events:");
			for (Event e : EventBus.getRegisteredEvents()) {
				sb.append("\n" + e.getName() + ":" + e.getEventId());
			}
			return sb.toString();
		}
		String[] sps = info.split(" ", 3);
		switch (sps[0]) {
		case "post":
			try {
				if (sps.length == 3) {
					Event e = EventBus.getEvent(UUID.fromString(sps[1]));
					JSONObject obj = JSON.parseObject(sps[2]);
					obj.forEach((s, o) -> {
						int index = Integer.parseInt(s);
						e.putExtra(index, o);
					});
					EventBus.postEvent(e);
				} else
					EventBus.postEvent(UUID.fromString(sps[1]));
			} catch (Exception e1) {
				throw new CommandException(e1.getMessage());
			}
			return "Event " + sps[1] + " has been posted.";
		}
		throw new CommandException("Unknown Command");
	}

}
