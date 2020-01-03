package com.chemistrylab.debug;

import java.util.*;
import java.awt.*;
import java.awt.datatransfer.*;
import com.chemistrylab.init.*;
import com.chemistrylab.layer.container.*;

public class ContainerCommand extends Command {

	private static final ContainerLoader LOADER = InitLoader.getContainerLoader();

	@Override
	public String invokeCommand(String info) throws CommandException {
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
				if(split.length == 6){
					String json = split[5];
					abc.specials(json);
				}
				Containers.addContainer(abc);
				Transferable trans = new StringSelection(abc.getUUID().toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
				return "Container Added.UUID="+abc.getUUID();
			case "remove":
				String uuid = split[1];
				Containers.removeContainer(uuid);
				return "Removed Container";
			case "info-container":
				String model0 = split[1];
				Map<String, Size> sizes0 = LOADER.getSizes(model0);
				StringBuilder ms = new StringBuilder("Container Model:"+model0);
				for(Map.Entry<String, Size> en:sizes0.entrySet()){
					ms.append("\n"+en.getValue());
				}
				return ms.toString();
			}
		} catch (Exception e) {
			throw new CommandException(e.getMessage());
		}
		throw new CommandException("Unknown Command");
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
		if(begin != in.length())
			al.add(in.substring(begin).trim());
		// To array
		Object[] o = al.toArray();
		String[] over = Arrays.copyOf(o, o.length, String[].class);
		return over;
	}
}
