package com.github.nickid2018.chemistrylab.reaction.data;

import com.google.common.eventbus.*;
import com.github.nickid2018.chemistrylab.event.*;
import com.github.nickid2018.chemistrylab.util.pool.*;
import com.github.nickid2018.chemistrylab.container.*;
import com.github.nickid2018.chemistrylab.properties.*;

public class Unit implements Poolable {

	/** Unit: mol */
	public static final int UNIT_MOLE = 0x0;
	/** Unit: g */
	public static final int UNIT_G = 0x1;
	/** Unit: L */
	public static final int UNIT_L = 0x2;

	public static final Unit NULL_UNIT = new Unit(null, UNIT_MOLE, 0);

	private int unit;
	private double num;
	private boolean isListen = true;
	private ChemicalItem chemical;

	public Unit() {
		isListen = false;
	}

	public Unit(ChemicalItem chem, String unit, double num) {
		this(chem, unitFromString(unit), num);
	}

	public Unit(ChemicalItem chem, int unit, double num) {
		chemical = chem;
		this.unit = unit;
		checkUnit();
		this.num = num;
		AbstractContainer.CHEMICAL_BUS.register(this);
	}

	public static final int unitFromString(String v) {
		if (v.equalsIgnoreCase("mol"))
			return UNIT_MOLE;
		if (v.equalsIgnoreCase("g"))
			return UNIT_G;
		if (v.equalsIgnoreCase("l"))
			return UNIT_L;
		throw new IllegalArgumentException("Illegal Unit " + v);
	}

	public static final String unitToString(int unit) {
		switch (unit) {
		case UNIT_MOLE:
			return "mol";
		case UNIT_G:
			return "g";
		case UNIT_L:
			return "L";
		}
		throw new IllegalArgumentException("Illegal Unit " + unit);
	}

	void checkUnit() {
		if (unit != UNIT_MOLE && unit != UNIT_G && unit != UNIT_L)
			throw new IllegalArgumentException("Illegal Unit " + unit);
	}

	public Unit setNotListen() {
		AbstractContainer.CHEMICAL_BUS.unregister(this);
		isListen = false;
		return this;
	}

	public ChemicalItem getChemical() {
		return chemical;
	}

	public int getUnit() {
		return unit;
	}

	public double getNum() {
		return num;
	}

	public void setNum(double num) {
		this.num = num;
	}

	public double toMol() {
		switch (unit) {
		case UNIT_MOLE:
			return num;
		case UNIT_G:
			return num / chemical.resource.getMess();
		case UNIT_L:
			return num / Environment.getGasMolV();
		}
		return -1;
	}

	public void fromMol(double mol) {
		switch (unit) {
		case UNIT_MOLE:
			num = mol;
			break;
		case UNIT_G:
			num = mol * chemical.resource.getMess();
			break;
		case UNIT_L:
			num = mol * Environment.getGasMolV();
			break;
		}
	}

	public Unit add(Unit other) {
		int ounit = other.unit;
		if (ounit == unit) {
			num += other.num;
		} else {
			// If it's different, first is to cast in MOL
			double mol = toMol() + other.toMol();
			fromMol(mol);
		}
		return this;
	}

	public boolean isListen() {
		return isListen;
	}

	@Override
	public String toString() {
		return chemical == null ? "" : chemical.toString() + ":" + num + unitToString(unit);
	}

	@Subscribe
	public void listen(EnvironmentEvent e) {
		if (e.changedItem.equals("gasmolv") && unit == UNIT_L) {
			num = num / ((DoubleProperty) e.oldValue).getValue() * Environment.getGasMolV();
		}
	}

	public Unit set(ChemicalItem chem, int unit, double num) {
		chemical = chem;
		this.unit = unit;
		checkUnit();
		this.num = num;
		return this;
	}

	@Override
	public void reset() {
		unit = -1;
		num = -1;
		chemical = null;
	}

	@Override
	public Unit clone() {
		return new Unit(chemical, unit, num);
	}
}
