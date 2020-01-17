package com.chemistrylab.reaction;

import java.io.*;
import java.util.*;
import com.cj.rdt.*;

public class Reactions extends HashMap<Integer,Reaction>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5216395493958762741L;
	
	public void writeToFile() throws IOException{
		RDTFile file = new RDTFile(Reaction.class.getResource("/assets/models/reaction/reactions.rdt").getFile());
		for(Reaction r:values()){
			ReactionEntry re = new ReactionEntry();
			re.setToWrite(r);
			file.addTag(re);
		}
		file.writeRDT();
	}
}
