package com.chemistrylab.debug;

import java.util.*;

import com.chemistrylab.util.Message;

public class CommandController {

	private static final Map<String, Command> commap = new HashMap<>();

	public static final Message[] runCommand(String command) throws CommandException {
		if (command.equals("crash"))
			throw new Error("Manually Crash");
		String[] sa = command.split(" ", 2);
		String head = sa[0];
		Command c = commap.get(head);
		if (c == null) {
			throw new CommandException("Can't find command " + head);
		} else
			return c.invokeCommand(sa[1]);
	}

	public static final void addCommandDecomper(String head, Command com) {
		commap.put(head, com);
	}

	public static final void removeCommandDecomper(String head) {
		commap.remove(head);
	}

	public static final void removeCommandDecomper(Command c) {
		String find = null;
		for (Map.Entry<String, Command> en : commap.entrySet()) {
			if (en.getValue().equals(c)) {
				find = en.getKey();
				break;
			}
		}
		if (find != null) {
			commap.remove(find);
		}
	}

	public static String[] split(String in) {
		ArrayList<String> al = new ArrayList<>();
		boolean isStr = false;
		boolean isRound = false;
		int begin = 0;
		for (int i = 0; i < in.length(); i++) {
			char at = in.charAt(i);
			// "
			if (at == '"' && !isRound)
				isStr = !isStr;
			// {
			if (at == '{' && !isStr)
				isRound = true;
			// }
			if (at == '}' && !isStr)
				isRound = false;
			if (at == ' ' && !isStr && !isRound) {
				al.add(in.substring(begin, i).trim());
				begin = i;
			}
		}
		if (begin != in.length())
			al.add(in.substring(begin).trim());
		// To array
		Object[] o = al.toArray();
		String[] over = Arrays.copyOf(o, o.length, String[].class);
		return over;
	}

	static {
		addCommandDecomper("tick", new TickerCommand());
		addCommandDecomper("eventbus", new EventBusComand());
		addCommandDecomper("container", new ContainerCommand());
		addCommandDecomper("environment", new EnvironmentCommand());
		addCommandDecomper("chemical", new ChemicalCommand());
	}
}
