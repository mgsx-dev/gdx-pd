package net.mgsx.pd.demo;

import com.badlogic.gdx.utils.Array;

public class Division{
	
	public static final Division quarterNote = new Division("Quarter", 1); // TODO eighteen ?
	public static final Division halfNote = new Division("Half", 2);
	public static final Division wholeNote = new Division("Whole", 4);
	public static final Division bar2 = new Division("2x", 8);
	public static final Division bar4 = new Division("4x", 16);
	public static final Division bar8 = new Division("8x", 32);
	public static final Division bar16 = new Division("16x", 64);
	
	public static final Array<Division> all = new Array<Division>(new Division[]{
		quarterNote, 
		halfNote, 
		wholeNote, 
		bar2, 
		bar4, 
		bar8, 
		bar16
	});
	
	public String name;
	public int value;
	public Division(String name, int value) {
		super();
		this.name = name;
		this.value = value;
	}
	@Override
	public String toString() {
		return name;
	}
}