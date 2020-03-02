package com.github.nickid2018.chemistrylab.reaction;

import java.io.*;
import java.util.*;

import com.cj.rdt.RDTFile;
import com.cj.rdt.RDTObject;
import com.github.nickid2018.chemistrylab.reaction.Reactions.*;
import com.github.nickid2018.chemistrylab.util.*;

import org.apache.commons.io.*;

public class Reactions extends HashMap<Integer, ReactionsEntry> {

	public static final class ReactionsEntry extends ArrayList<Reaction> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -927571258799517740L;

	}

	private int size = 0;

	public void addNewReaction(Reaction re) throws IOException {
		put(re.computeSign(), re);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		InputStream res_rdt = ResourceManager.getResourceAsStream("default:assets/models/reaction/reactions.rdt");
		IOUtils.copy(res_rdt, buffer);
		DataOutputStream out = new DataOutputStream(buffer);
		ReactionEntry r = new ReactionEntry();
		r.setToWrite(re);
		r.writeTag(out, new RDTObject<>(new RDTFile("", RDTFile.RDT11)));
		OutputStream rdt_out = ResourceManager.getOutputStream("default:assets/models/reaction/reactions.rdt");
		buffer.writeTo(rdt_out);
		ResourceManager.flushStream();
	}

	public void put(int computeSign, Reaction r) {
		ReactionsEntry entry = get(computeSign);
		if (entry == null) {
			entry = new ReactionsEntry();
			entry.add(r);
			put(computeSign, entry);
		} else {
			entry.add(r);
		}
		size++;
	}

	@Override
	public int size() {
		return size;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5216395493958762741L;
}
