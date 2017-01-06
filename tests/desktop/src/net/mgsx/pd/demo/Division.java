package net.mgsx.pd.demo;

import com.badlogic.gdx.utils.Array;

public class Division{
	public static Array<Division> all(){
		 Array<Division> a = new Array<Division>();
		 a.addAll(new Division("Quarter", 1), 
				 new Division("Half", 2), 
				 new Division("Full", 4), 
				 new Division("1/2x", 8), 
				 new Division("Bar", 16), 
				 new Division("2x", 32), 
				 new Division("4x", 64));
		 return a;
	}
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