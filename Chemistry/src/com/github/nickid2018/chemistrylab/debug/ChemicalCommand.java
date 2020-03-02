package com.github.nickid2018.chemistrylab.debug;

import com.github.nickid2018.chemistrylab.chemicals.*;
import com.github.nickid2018.chemistrylab.layer.container.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.util.*;

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
