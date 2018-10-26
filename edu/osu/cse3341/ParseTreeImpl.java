package edu.osu.cse3341;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseTreeImpl implements ParseTree {

	private class Node {
		int alt;
		NodeType type;
		List<Integer> children;
		int parent;
		int value;
		String name;

		private Node() {
			alt = 1;
			children = new ArrayList<>();
		}
	}

	List<Node> tree;
	int cursor;

	static Map<String, Integer> st;

	public ParseTreeImpl() {
		cursor = 0;
		tree = new ArrayList<>();
		tree.add(new Node());

		st = new HashMap<>();
	}

	@Override
	public int getAlt() {
		return tree.get(cursor).alt;
	}

	@Override
	public void setAlt(int alt) {
		tree.get(cursor).alt = alt;
	}

	@Override
	public int getValue() {
		assert this.getNodeType() == NodeType.valueOf("ID")
				|| this.getNodeType() == NodeType.valueOf("INT") : "Cursor must be an ID node";

		int val = Integer.MIN_VALUE;
		if (this.getNodeType() == NodeType.valueOf("INT")) {
			val = tree.get(cursor).value;
		} else {
			String id = this.getIdString();
			val = st.get(id);
		}
		return val;
	}

	@Override
	public void setValue(int value) {
		assert this.getNodeType() == NodeType.valueOf("ID")
				|| this.getNodeType() == NodeType.valueOf("INT") : "Cursor must be an ID node";
		if (this.getNodeType() == NodeType.valueOf("INT")) {
			tree.get(cursor).value = value;
		} else {
			String id = this.getIdString();
			st.put(id, value);
		}
	}

	@Override
	public NodeType getNodeType() {
		return tree.get(cursor).type;
	}

	@Override
	public void setNodeType(NodeType type) {
		tree.get(cursor).type = type;
	}

	@Override
	public String getIdString() {
		assert this.getNodeType() == NodeType.valueOf("ID") : "Cursor must be an ID node";

		// TODO handle ST?
		return tree.get(cursor).name;
	}

	@Override
	public void setIdString(String id) {
		tree.get(cursor).name = id;

		if (!st.containsKey(id)) {
			st.put(id, Integer.MIN_VALUE);
		}
	}

	@Override
	public void addChild() {
		Node temp = new Node();
		temp.parent = cursor;
		tree.get(cursor).children.add(tree.size());

		tree.add(temp);
	}

	@Override
	public void moveToChild(int index) {
		cursor = tree.get(cursor).children.get(index);
	}

	@Override
	public void moveToParent() {
		// Node parent = tree.get(tree.get(cursor).parent);
		cursor = tree.get(cursor).parent;
	}

	@Override
	public int getChildCount() {
		return tree.get(cursor).children.size();
	}

	@Override
	public boolean hasParent() {
		return tree.get(cursor).parent >= 0;
	}

	@Override
	public boolean isDeclared(String id) {
		return st.containsKey(id);
	}

	@Override
	public boolean isInitialized(String id) {
		// TODO need more
		return isDeclared(id);
	}
}