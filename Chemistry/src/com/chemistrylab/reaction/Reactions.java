package com.chemistrylab.reaction;

import java.io.*;
import java.util.*;
import com.cj.rdt.*;
import com.chemistrylab.util.*;
import com.chemistrylab.reaction.Reactions.*;

public class Reactions extends HashMap<Integer,ReactionsEntry>{

	public static final class ReactionsEntry extends ArrayList<Reaction>{

		/**
		 * 
		 */
		private static final long serialVersionUID = -927571258799517740L;
		
	}
	
	private int size = 0;
	
	public void writeToFile() throws IOException{
		RDTFile file = new RDTFile(ResourceManager.getResource("/assets/models/reaction/reactions.rdt").getFile());
		for(ReactionsEntry ree:values()){
			for(Reaction r:ree){
				ReactionEntry re = new ReactionEntry();
				re.setToWrite(r);
				file.addTag(re);
			}
		}
		file.writeRDT();
	}

	public void put(int computeSign, Reaction r) {
		ReactionsEntry entry = get(computeSign);
		if(entry == null){
			entry = new ReactionsEntry();
			entry.add(r);
			put(computeSign,entry);
		}else{
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
