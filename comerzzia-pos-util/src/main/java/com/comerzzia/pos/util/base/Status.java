package com.comerzzia.pos.util.base;

public class Status {

	public static final int UNMODIFIED = 0;
	public static final int MODIFIED = 1;
	public static final int DELETED = 2;
	public static final int NEW = 3;

	public int getUNMODIFIED() {
		return UNMODIFIED;
	}

	public int getMODIFIED() {
		return MODIFIED;
	}

	public int getDELETED() {
		return DELETED;
	}

	public int getNEW() {
		return NEW;
	}
}
