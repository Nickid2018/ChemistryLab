package com.chemistrylab.debug;

import com.chemistrylab.util.*;
import com.chemistrylab.reaction.*;
import com.chemistrylab.chemicals.*;
import com.chemistrylab.layer.container.*;

public class ChemicalCommand extends Command {

	@Override
	public Message[] invokeCommand(String info) throws CommandException {
		String[] split = CommandController.split(info);
		try {
			switch (split[0]) {
			case "add":
				// Format: chemical add {uuid} {chemical} {unit} {count}
				String container = split[1];
				AbstractContainer con = Containers.getContainer(container);
				ChemicalResource chem = ChemicalsLoader.chemicals.get(split[2]);
				String unit = split[3];
				String count = split[4];
				double num = Double.parseDouble(count);
				Unit add = new Unit(chem, unit, num).setNotListen();
				con.addChemical(add);
				return new Message[] { new Message().addMessageEntry(
						new MessageEntry(String.format(I18N.getString("command.chemical.add"), add, container))) };
			}
		} catch (Exception e) {
			throw new CommandException(e.getMessage());
		}
		return null;
	}
}
