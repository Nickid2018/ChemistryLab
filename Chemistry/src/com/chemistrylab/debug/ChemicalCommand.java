package com.chemistrylab.debug;

import java.util.*;
import com.chemistrylab.reaction.*;
import com.chemistrylab.chemicals.*;
import com.chemistrylab.layer.container.*;

public class ChemicalCommand extends Command {

	@Override
	public String invokeCommand(String info) throws CommandException {
		String[] split = split(info);
		try {
			switch (split[0]) {
			case "add":
				String container = split[1];
				AbstractContainer con = Containers.getContainer(container);
				ChemicalResource chem = ChemicalsLoader.chemicals.get(split[2]);
				String unit = split[3];
				String count = split[4];
				double num = Double.parseDouble(count);
				Unit add = new Unit(chem, unit, num).setNotListen();
				con.addChemical(add);
				return "Added " + add + " into container " + container;
			}
		} catch (Exception e) {
			throw new CommandException(e.getMessage());
		}
		return null;
	}

	public String[] split(String in) {
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
}
