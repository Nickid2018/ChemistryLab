package com.chemistrylab.reaction;

import java.io.*;
import java.util.*;
import com.cj.rdt.*;
import com.cj.jmcl.*;
import com.cj.rdt.tag.*;
import com.cj.rdt.versions.*;
import com.chemistrylab.chemicals.*;

public class ReactionEntry extends RDTTagBase {
	
	public static final byte REACTION_ENTRY = 0x10;
	public static final ReactionEntry INSTANCE =new ReactionEntry();

	private Reaction toWrite;

	public void setToWrite(Reaction toWrite) {
		this.toWrite = toWrite;
	}

	@Override
	public RDTWarn readTag(DataInput inp, RDTObject<?> o) throws IOException {
		Reactions os = (Reactions) o.getObject();
		// Sign of reaction type 0=non-reversible reaction 1=reversible reaction
		byte sign = inp.readByte();
		// Things to react
		inp.readByte();
		@SuppressWarnings("unchecked")
		TagArray<TagPair> reacts = (TagArray<TagPair>) o.getRDTFile().getVersion().tryTag(RDT11.TAG_ARRAY);
		reacts.readTag(inp, o);
		Map<ChemicalResource, Integer> react = new HashMap<>();
		ArrayList<TagPair> reactss = reacts.getTags();
		for (TagPair p : reactss) {
			react.put(ChemicalsLoader.chemicals.get(p.getKey().toString()), ((TagInt) p.getValue()).getVal());
		}
		// Things to get
		inp.readByte();
		@SuppressWarnings("unchecked")
		TagArray<TagPair> gets = (TagArray<TagPair>) o.getRDTFile().getVersion().tryTag(RDT11.TAG_ARRAY);
		gets.readTag(inp, o);
		Map<ChemicalResource, Integer> get = new HashMap<>();
		ArrayList<TagPair> getss = gets.getTags();
		for (TagPair p : getss) {
			get.put(ChemicalsLoader.chemicals.get(p.getKey().toString()), ((TagInt) p.getValue()).getVal());
		}
		// Conditions (K,deltaH,deltaS)
		double dH = inp.readDouble();
		double dS = inp.readDouble();
		if (sign == 1) {
			String K = inp.readUTF();
			ReversibleReaction re;
			try {
				re = new ReversibleReaction(react, get, dH, dS, K);
			} catch (MathException e) {
				throw new IOException(e);
			}
			os.put(re.computeSign(), re);
		} else {
			NonReversibleReaction re = new NonReversibleReaction(react, get, dH, dS);
			os.put(re.computeSign(), re);
		}
		return RDTWarn.NO_WARN;
	}

	@Override
	public void writeTag(DataOutput oup, RDTObject<?> o) throws IOException {//Sign write
		oup.writeByte(REACTION_ENTRY);
		if (toWrite instanceof ReversibleReaction) {
			oup.writeByte(1);
		} else {
			oup.writeByte(0);
		}
		TagArray<TagPair> react = new TagArray<>();
		for(Map.Entry<ChemicalResource, Integer> en:toWrite.getReacts().entrySet()){
			TagString key = new TagString(en.getKey().getFinalName());
			TagInt value = new TagInt(en.getValue());
			TagPair pair = new TagPair(key,value);
			react.addTag(pair);
		}
		react.writeTag(oup, o);
		TagArray<TagPair> get = new TagArray<>();
		for(Map.Entry<ChemicalResource, Integer> en:toWrite.getGets().entrySet()){
			TagString key = new TagString(en.getKey().getFinalName());
			TagInt value = new TagInt(en.getValue());
			TagPair pair = new TagPair(key,value);
			get.addTag(pair);
		}
		get.writeTag(oup, o);
		oup.writeDouble(toWrite.getdH());
		oup.writeDouble(toWrite.getdS());
		if (toWrite instanceof ReversibleReaction) {
			oup.writeUTF(((ReversibleReaction)toWrite).getStrK());
		}
	}

	@Override
	public RDTTagBase createNew() {
		return new ReactionEntry();
	}

	@Override
	public String tagName() {
		return "REACTION_ENTRY";
	}

	@Override
	public String toString() {
		return "Reaction";
	}

}
