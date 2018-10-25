package edu.osu.cse3341;

import java.util.ArrayList;
import java.util.List;

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

	public ParseTreeImpl() {
		cursor = 0;
		tree = new ArrayList<>();
		tree.add(new Node());
	}

	@Override
	public int getAlt() {
		return tree.get(cursor).alt;
	}

	@Override
	public NodeType getNodeType() {
		return tree.get(cursor).type;
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
	public void moveToParent() {
		Node parent = tree.get(tree.get(cursor).parent);
		cursor = tree.get(cursor).parent;
	}

	@Override
	public void moveToChild(int index) {
		cursor = tree.get(cursor).children.get(index);
	}

	@Override
	public String getIdString() {
		assert this.getNodeType() == NodeType.valueOf("ID") : "Cursor must be an ID node";

		return tree.get(cursor).name;
	}

	@Override
	public void setIdValue(int value) {
		assert this.getNodeType() == NodeType.valueOf("ID") : "Cursor must be an ID node";
		tree.get(cursor).value = value;
	}

	@Override
	public int getIdValue() {
		assert this.getNodeType() == NodeType.valueOf("ID") : "Cursor must be an ID node";
		return tree.get(cursor).value;
	}

	@Override
	public void setAlt(int alt) {
		tree.get(cursor).alt = alt;
	}

	@Override
	public void setNodeType(NodeType type) {
		tree.get(cursor).type = type;
	}

	@Override
	public void addChild() {
		Node temp = new Node();
		temp.parent = cursor;
		tree.get(cursor).children.add(tree.size());

		tree.add(temp);
	}

	@Override
	public void setIdString(String id) {
		tree.get(cursor).name = id;
	}
}