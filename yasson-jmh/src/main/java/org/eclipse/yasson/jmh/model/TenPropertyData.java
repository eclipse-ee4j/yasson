package org.eclipse.yasson.jmh.model;

import java.util.*;

public class TenPropertyData{
	private final String prop1;
	private final String prop2;
	private final int prop3;
	private final double prop4;
	private final String[] prop5;
	private final List<Integer> prop6;
	private final String prop7;
	private final int prop8;
	private final int[] prop9;
	private final long prop10;
	
	public TenPropertyData(String prop1, String prop2, int prop3, double prop4, String[] prop5, List<Integer> prop6,
			String prop7, int prop8, int[] prop9, long prop10) {
		this.prop1 = prop1;
		this.prop2 = prop2;
		this.prop3 = prop3;
		this.prop4 = prop4;
		this.prop5 = prop5;
		this.prop6 = prop6;
		this.prop7 = prop7;
		this.prop8 = prop8;
		this.prop9 = prop9;
		this.prop10 = prop10;
	}

	public String getProp1() {
		return prop1;
	}

	public String getProp2() {
		return prop2;
	}

	public int getProp3() {
		return prop3;
	}

	public double getProp4() {
		return prop4;
	}

	public String[] getProp5() {
		return prop5;
	}

	public List<Integer> getProp6() {
		return prop6;
	}

	public String getProp7() {
		return prop7;
	}

	public int getProp8() {
		return prop8;
	}

	public int[] getProp9() {
		return prop9;
	}

	public long getProp10() {
		return prop10;
	}
}