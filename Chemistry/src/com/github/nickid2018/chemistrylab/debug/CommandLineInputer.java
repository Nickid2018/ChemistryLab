package com.github.nickid2018.chemistrylab.debug;

import java.util.*;
import org.apache.log4j.*;
import com.github.nickid2018.chemistrylab.util.message.*;

public class CommandLineInputer {

	private static Thread inputThread;
	private static Logger logger = Logger.getLogger("Command Inputer");

	public static void init() {
		inputThread = new Thread(() -> {
			try (Scanner scan = new Scanner(System.in)) {
				while (true) {
					String command = scan.nextLine();
					try {
						Message[] messages = CommandController.runCommand(command);
						for (Message mess : messages) {
							logger.info(mess.getText());
						}
					} catch (Throwable e) {
						logger.error(e);
					}
				}
			}
		}, "Command Line Commander");
		inputThread.setDaemon(true);
		inputThread.start();
	}
}
