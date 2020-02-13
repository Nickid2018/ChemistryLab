package com.chemistrylab.debug;

import java.util.*;
import java.awt.*;
import org.lwjgl.input.*;
import java.awt.datatransfer.*;
import com.chemistrylab.init.*;
import com.chemistrylab.util.*;
import org.newdawn.slick.Color;
import com.chemistrylab.layer.container.*;

public class ContainerCommand extends Command {

	private static final ContainerLoader LOADER = InitLoader.getContainerLoader();

	@Override
	public Message[] invokeCommand(String info) throws CommandException {
		String[] split = split(info);
		try {
			switch (split[0]) {
			case "add":
				String model = split[1];
				Map<String, Size> sizes = LOADER.getSizes(model);
				String si = split[2];
				Size s = sizes.get(si);
				int x0 = Integer.parseInt(split[3]);
				int y0 = Integer.parseInt(split[4]);
				AbstractContainer abc = LOADER.newContainer(model, x0, y0, s);
				if (split.length == 6) {
					String json = split[5];
					abc.specials(json);
				}
				Containers.addContainer(abc);
				return new Message[] { new Message()
						.addMessageEntry(new MessageEntry(I18N.getString("command.container.add"))).addMessageEntry(
								new MessageEntry("UUID = " + abc.getUUID()).setUnderline(true).setClickEvent(() -> {
									if (!Mouse.isButtonDown(0))
										return;
									Transferable trans = new StringSelection(abc.getUUID().toString());
									Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
								})) };
			case "remove":
				String uuid = split[1];
				Containers.removeContainer(uuid);
				return new Message[] { new Message()
						.addMessageEntry(new MessageEntry(I18N.getString("command.container.remove")))
						.addMessageEntry(new MessageEntry("UUID = " + uuid).setUnderline(true).setClickEvent(() -> {
							if (!Mouse.isButtonDown(0))
								return;
							Transferable trans = new StringSelection(uuid.toString());
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
						})) };
			case "info-container":
				String model0 = split[1];
				Map<String, Size> sizes0 = LOADER.getSizes(model0);
				Message[] ms_r = new Message[sizes0.size() + 2];
				ms_r[0] = new Message().addMessageEntry(
						new MessageEntry(String.format(I18N.getString("command.container.model"), split[1]))
								.setColor(Color.yellow));
				int i = 1;
				for (Map.Entry<String, Size> en : sizes0.entrySet()) {
					ms_r[i++] = new Message().addMessageEntry(new MessageEntry(en.getValue() + ""));
				}
				ms_r[i] = new Message()
						.addMessageEntry(new MessageEntry(I18N.getString("command.end")).setColor(Color.yellow));
				return ms_r;
			}
		} catch (Exception e) {
			throw new CommandException(e.getMessage());
		}
		throw new CommandException(I18N.getString("command.unknown"));
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
