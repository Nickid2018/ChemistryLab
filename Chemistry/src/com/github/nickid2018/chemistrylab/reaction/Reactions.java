package com.github.nickid2018.chemistrylab.reaction;

import java.io.*;
import java.util.*;
import com.google.common.base.*;
import com.github.nickid2018.rdt.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.reaction.Reactions.*;

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
		Preconditions.checkNotNull(re, "Reaction can't be null");
		put(re.computeSign(), re);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		InputStream res_rdt = ResourceManager.getResourceAsStream("default:assets/models/reaction/reactions.rdt");
		IOUtils.copy(res_rdt, buffer);
		DataOutputStream out = new DataOutputStream(buffer);
		new ReactionEntry().setToWrite(re).writeTag(out, new RDTObject<>(new RDTFile("", RDTFile.RDT11)));
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
