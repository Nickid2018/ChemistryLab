package com.chemistrylab.reaction;

import com.chemistrylab.chemicals.*;

public class Unit {

	/** Unit: mol */
	public static final int UNIT_MOLE = 0x0;
	/** Unit: g */
	public static final int UNIT_G = 0x1;
	/** Unit: L */
	public static final int UNIT_L = 0x2;

	private final Chemical chemical;
	private final int unit;
	private double num;

	public Unit(Chemical chem, int unit, double num) {
		chemical = chem;
		this.unit = unit;
		checkUnit();
		this.num = num;
	}

	void checkUnit() throws RuntimeException {
		if (unit != UNIT_MOLE && unit != UNIT_G && unit != UNIT_L)
			throw new RuntimeException("Illegal Unit " + unit);
	}

	public Chemical getChemical() {
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
			return num / (chemical.getMess());
		case UNIT_L:
			return num / Environment.getGasMolV();
		}
		return -1;
	}

	public Unit add(Unit other) {
		int ounit = other.unit;
		if (ounit == unit) {
			num += other.num;
		} else {
			// If it's different, first is to cast in MOL

		}
		return this;
	}
}
