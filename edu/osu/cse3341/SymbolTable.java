package edu.osu.cse3341;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
	private class Info {
		int val;
		boolean initialized;

		public Info() {
			val = 0;
			initialized = false;
		}
	}

	Map<String, Info> data;

	public SymbolTable() {
		data = new HashMap<>();
	}

	public boolean isInitialized(String id) {
		return data.get(id).initialized;
	}

	public int getValue(String id) {
		return data.get(id).val;
	}

	public void setValue(String id, int val) {
		data.get(id).val = val;
	}

	public void initialize(String id) {
		data.get(id).initialized = true;
	}

	public void add(String id) {
		data.put(id, new Info());
	}

	public boolean hasId(String id) {
		return data.containsKey(id);
	}
}
