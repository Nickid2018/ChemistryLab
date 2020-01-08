package com.chemistrylab.reaction;

import com.chemistrylab.eventbus.*;
import com.chemistrylab.chemicals.*;

public class Unit implements EventBusListener {

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
			return num / chemical.getMess();
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
			num = mol * chemical.getMess();
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

	@Override
	public boolean receiveEvents(Event e) {
		return e.equals(Environment.ENVIRONMENT_CHANGED);
	}

	@Override
	public void listen(Event e) {
		Environment.EventEnvironmentChanged ev = (Environment.EventEnvironmentChanged) e;
		if (ev.getChangedItem().equals("gasmolv") && unit == UNIT_L) {
			double old = (Double) ev.getOldValue().getValue();
			num = num / old * Environment.getGasMolV();
		}
	}
}
