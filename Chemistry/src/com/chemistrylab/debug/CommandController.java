package com.chemistrylab.debug;

import java.util.*;

public class CommandController {
	
	private static final Map<String,Command> commap = new HashMap<>();

	public static final String runCommand(String command) throws CommandException{
		String[] sa = command.split(" ", 2);
		String head = sa[0];
		Command c = commap.get(head);
		if(c == null){
			throw new CommandException("Can't find command " + head);
		}else
			return c.invokeCommand(sa[1]);
	}
	
	public static final void addCommandDecomper(String head,Command com){
		commap.put(head, com);
	}
	
	public static final void removeCommandDecomper(String head){
		commap.remove(head);
	}
	
	public static final void removeCommandDecomper(Command c){
		String find = null;
		for(Map.Entry<String, Command> en:commap.entrySet()){
			if(en.getValue().equals(c)){
				find = en.getKey();
				break;
			}
		}
		if(find != null){
			commap.remove(find);
		}
	}
	
	static{
		addCommandDecomper("tick", new TickerCommand());
		addCommandDecomper("eventbus", new EventBusComand());
	}
}
