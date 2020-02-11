package com.chemistrylab.debug;

import java.util.*;
import java.awt.Toolkit;
import org.lwjgl.input.*;
import org.newdawn.slick.*;
import com.alibaba.fastjson.*;
import java.awt.datatransfer.*;
import com.chemistrylab.util.*;
import com.chemistrylab.eventbus.*;

public class EventBusComand extends Command {

	@Override
	public Message[] invokeCommand(String info) throws CommandException {
		switch (info) {
		case "nonwait-size":
			return new Message[] { new Message()
					.addMessageEntry(new MessageEntry("EventBus Nonwait Size:" + EventBus.getNonawaitSize())) };
		case "nonwait-pass":
			return new Message[] { new Message().addMessageEntry(
					new MessageEntry("EventBus Nonwait Passed Size:" + EventBus.getPassedNonwaitEvents())) };
		case "await-units":
			return new Message[] { new Message().addMessageEntry(
					new MessageEntry("EventBus Await Unit Size:" + EventBus.getAvailableAwaitUnits())) };
		case "reg-events":
			Collection<Event> events = EventBus.getRegisteredEvents();
			Message[] ms_r = new Message[events.size() + 2];
			ms_r[0] = new Message()
					.addMessageEntry(new MessageEntry("EventBus Registered Events:").setColor(Color.yellow));
			int i0 = 1;
			for (Event e : events) {
				ms_r[i0++] = new Message().addMessageEntry(new MessageEntry(e.getName() + " : "))
						.addMessageEntry(new MessageEntry(e.getEventId() + "").setClickEvent(() -> {
							if(!Mouse.isButtonDown(0))
								return;
							Transferable trans = new StringSelection(e.getEventId().toString());
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
						}).setUnderline(true));
			}
			ms_r[i0] = new Message().addMessageEntry(new MessageEntry("---End---").setColor(Color.yellow));
			return ms_r;
		case "now-events":
			Map<Event.CompleteComparedEvent, Integer> evsnap = EventBus.getNowActiveEvents();
			Message[] ms = new Message[evsnap.size() + 2];
			ms[0] = new Message().addMessageEntry(new MessageEntry("---Now Events---").setColor(Color.yellow));
			int i = 1;
			for (Map.Entry<Event.CompleteComparedEvent, Integer> en : evsnap.entrySet()) {
				ms[i++] = new Message().addMessageEntry(new MessageEntry(en.getKey() + " : " + en.getValue()));
			}
			ms[i] = new Message().addMessageEntry(new MessageEntry("---End---").setColor(Color.yellow));
			return ms;
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
			return new Message[] {
					new Message().addMessageEntry(new MessageEntry("Event " + sps[1] + " has been posted.")) };
		}
		throw new CommandException("Unknown Command");
	}

}
