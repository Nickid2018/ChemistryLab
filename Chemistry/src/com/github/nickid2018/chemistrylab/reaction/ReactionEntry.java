package com.github.nickid2018.chemistrylab.reaction;

import java.io.*;
import java.util.*;
import com.github.nickid2018.rdt.*;
import com.github.nickid2018.jmcl.*;
import com.github.nickid2018.rdt.tag.*;
import com.github.nickid2018.rdt.versions.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class ReactionEntry extends RDTTagBase {

	public static final byte REACTION_ENTRY = 0x10;
	public static final ReactionEntry INSTANCE = new ReactionEntry();

	private Reaction toWrite;

	public ReactionEntry setToWrite(Reaction toWrite) {
		this.toWrite = toWrite;
		return this;
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
			react.put(ChemicalLoader.CHEMICALS.get(p.getKey().toString()), ((TagInt) p.getValue()).getVal());
		}
		// Things to get
		inp.readByte();
		@SuppressWarnings("unchecked")
		TagArray<TagPair> gets = (TagArray<TagPair>) o.getRDTFile().getVersion().tryTag(RDT11.TAG_ARRAY);
		gets.readTag(inp, o);
		Map<ChemicalResource, Integer> get = new HashMap<>();
		ArrayList<TagPair> getss = gets.getTags();
		for (TagPair p : getss) {
			get.put(ChemicalLoader.CHEMICALS.get(p.getKey().toString()), ((TagInt) p.getValue()).getVal());
		}
		// Conditions (K,deltaH,deltaS)
		double dH = inp.readDouble();
		double dS = inp.readDouble();
		if (sign == 1) {
			String k = inp.readUTF();
			String K = inp.readUTF();
			ReversibleReaction re;
			ReversibleReaction res;
			try {
				re = new ReversibleReaction(react, get, dH, dS, K, k);
				res = re.reverse();
			} catch (MathException e) {
				throw new IOException(e);
			}
			for (ChemicalResource r : react.keySet()) {
				r.addReaction(re);
			}
			for (ChemicalResource r : get.keySet()) {
				r.addReaction(res);
			}
			os.put(re.computeSign(), re);
		} else {
			String k = inp.readUTF();
			NonReversibleReaction re;
			try {
				re = new NonReversibleReaction(react, get, dH, dS, k);
			} catch (MathException e) {
				throw new IOException(e);
			}
			for (ChemicalResource r : react.keySet()) {
				r.addReaction(re);
			}
			os.put(re.computeSign(), re);
		}
		return RDTWarn.NO_WARN;
	}

	@Override
	public void writeTag(DataOutput oup, RDTObject<?> o) throws IOException {
		// Sign write
		oup.writeByte(REACTION_ENTRY);
		if (toWrite instanceof ReversibleReaction) {
			oup.writeByte(1);
		} else {
			oup.writeByte(0);
		}
		TagArray<TagPair> react = new TagArray<>();
		for (Map.Entry<ChemicalResource, Integer> en : toWrite.getReacts().entrySet()) {
			TagString key = new TagString(en.getKey().getFinalName());
			TagInt value = new TagInt(en.getValue());
			TagPair pair = new TagPair(key, value);
			react.addTag(pair);
		}
		react.writeTag(oup, o);
		TagArray<TagPair> get = new TagArray<>();
		for (Map.Entry<ChemicalResource, Integer> en : toWrite.getGets().entrySet()) {
			TagString key = new TagString(en.getKey().getFinalName());
			TagInt value = new TagInt(en.getValue());
			TagPair pair = new TagPair(key, value);
			get.addTag(pair);
		}
		get.writeTag(oup, o);
		oup.writeDouble(toWrite.getdH());
		oup.writeDouble(toWrite.getdS());
		oup.writeUTF(toWrite.strk);
		if (toWrite instanceof ReversibleReaction) {
			oup.writeUTF(((ReversibleReaction) toWrite).getStrK());
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
