package com.github.nickid2018.chemistrylab.debug;

import com.github.nickid2018.chemistrylab.util.message.*;

public abstract class Command {

	public abstract Message[] invokeCommand(String info) throws CommandException;
}
