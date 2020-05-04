package com.github.nickid2018.chemistrylab.debug;

import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.container.*;
import com.github.nickid2018.chemistrylab.chemicals.*;
import com.github.nickid2018.chemistrylab.util.message.*;
import com.github.nickid2018.chemistrylab.reaction.data.*;

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
				ChemicalResource chem = ChemicalLoader.CHEMICALS.get(split[2]);
				String unit = split[3];
				String count = split[4];
				double num = Double.parseDouble(count);
				Unit add = UnitGetter.newUnit(chem.getDefaultItem(), unit, num);
				con.addChemical(add);
				UnitGetter.free(add);
				return new Message[] { new Message().addMessageEntry(
						new MessageEntry(String.format(I18N.getString("command.chemical.add"), add, container))) };
			}
		} catch (Exception e) {
			throw new CommandException(e.getMessage());
		}
		return null;
	}
}
