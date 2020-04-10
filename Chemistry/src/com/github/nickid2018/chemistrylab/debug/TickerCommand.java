package com.github.nickid2018.chemistrylab.debug;

import com.github.nickid2018.chemistrylab.util.*;

public class TickerCommand extends Command {

	@Override
	public Message[] invokeCommand(String info) throws CommandException {
		switch (info) {
		case "start":
			// Format: ticker start
			Ticker.startTick();
			return new Message[] {
					new Message().addMessageEntry(new MessageEntry(I18N.getString("command.ticker.start"))) };
		case "stop":
			// Format: ticker stop
			Ticker.stopTick();
			return new Message[] {
					new Message().addMessageEntry(new MessageEntry(I18N.getString("command.ticker.stop"))) };
		case "reset":
			// Format: ticker reset
			Ticker.stopTick();
			Ticker.startTick();
			return new Message[] {
					new Message().addMessageEntry(new MessageEntry(I18N.getString("command.ticker.reset"))) };
		}
		throw new CommandException(I18N.getString("command.unknown"));
	}

}
