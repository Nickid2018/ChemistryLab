package com.chemistrylab.debug;

import com.chemistrylab.util.*;

public abstract class Command {

	public abstract Message[] invokeCommand(String info) throws CommandException;
}
