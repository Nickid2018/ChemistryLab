package com.chemistrylab.debug;

public abstract class Command {

	public abstract String invokeCommand(String info) throws CommandException;
}
