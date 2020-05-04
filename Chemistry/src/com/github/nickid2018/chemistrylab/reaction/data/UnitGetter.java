package com.github.nickid2018.chemistrylab.reaction.data;

import com.github.nickid2018.chemistrylab.util.pool.*;

public class UnitGetter {

	public static Unit newUnit(ChemicalItem chem, int u, double num) {
		Unit unit = Pools.obtain(Unit.class);
		unit.set(chem, u, num);
		return unit;
	}

	public static Unit newUnit(ChemicalItem chem, String u, double num) {
		return newUnit(chem, Unit.unitFromString(u), num);
	}

	public static void free(Unit... units) {
		for (Unit unit : units)
			Pools.free(unit);
	}
}
