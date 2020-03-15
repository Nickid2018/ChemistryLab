package com.github.nickid2018.chemistrylab.debug;

import java.util.*;
import java.awt.Toolkit;
import com.alibaba.fastjson.*;
import com.github.nickid2018.chemistrylab.eventbus.*;
import com.github.nickid2018.chemistrylab.util.*;

import java.awt.datatransfer.*;

public class EventBusComand extends Command {

	@Override
	public Message[] invokeCommand(String info) throws CommandException {
		switch (info) {
		case "nonwait-size":
			// Format: eventbus nonwait-size
			return new Message[] { new Message().addMessageEntry(new MessageEntry(
					String.format(I18N.getString("command.eventbus.nonwaitsize"), EventBus.getNonawaitSize()))) };
		case "nonwait-pass":
			// Format: eventbus nonwait-pass
			return new Message[] { new Message().addMessageEntry(new MessageEntry(String
					.format(I18N.getString("command.eventbus.passednonwait"), EventBus.getPassedNonwaitEvents()))) };
		case "await-units":
			// Format: eventbus await-units
			return new Message[] { new Message().addMessageEntry(new MessageEntry(String
					.format(I18N.getString("command.eventbus.awaitunitsize"), EventBus.getAvailableAwaitUnits()))) };
		case "reg-events":
			// Format: eventbus reg-events
			Collection<Event> events = EventBus.getRegisteredEvents();
			Message[] ms_r = new Message[events.size() + 2];
//			ms_r[0] = new Message().addMessageEntry(
//					new MessageEntry((I18N.getString("command.eventbus.regevents"))).setColor(Color.yellow));
			int i0 = 1;
			for (Event e : events) {
				ms_r[i0++] = new Message().addMessageEntry(new MessageEntry(e.getName() + " : "))
						.addMessageEntry(new MessageEntry(e.getEventId() + "").setClickEvent((button, action, mods) -> {
							if (button != 0)
								return;
							Transferable trans = new StringSelection(e.getEventId().toString());
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
						}).setUnderline(true));
			}
//			ms_r[i0] = new Message()
//					.addMessageEntry(new MessageEntry(I18N.getString("command.end")).setColor(Color.yellow));
			return ms_r;
		case "now-events":
			// Format: eventbus now-events
			Map<Event.CompleteComparedEvent, Integer> evsnap = EventBus.getNowActiveEvents();
			Message[] ms = new Message[evsnap.size() + 2];
//			ms[0] = new Message().addMessageEntry(
//					new MessageEntry(I18N.getString("command.eventbus.nowevents")).setColor(Color.yellow));
			int i = 1;
			for (Map.Entry<Event.CompleteComparedEvent, Integer> en : evsnap.entrySet()) {
				ms[i++] = new Message().addMessageEntry(new MessageEntry(en.getKey() + " : " + en.getValue()));
			}
//			ms[i] = new Message()
//					.addMessageEntry(new MessageEntry(I18N.getString("command.end")).setColor(Color.yellow));
			return ms;
		}
		String[] sps = info.split(" ", 3);
		switch (sps[0]) {
		case "post":
			// Format: eventbus post {uuid} {addition}
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
			return new Message[] { new Message().addMessageEntry(
					new MessageEntry(String.format(I18N.getString("command.eventbus.post"), sps[1]))) };
		}
		throw new CommandException(I18N.getString("command.unknown"));
	}

}
