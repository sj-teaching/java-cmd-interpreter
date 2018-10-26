package edu.osu.cse3341;

public interface ParseTree {
	public static enum NodeType {
		PROG, DS, SS, DECL, IDLIST, STMT, ASSIGN, IF, LOOP, IN, OUT, COND, COMP, EXP, TERM, FAC, COMPOP, ID, INT
	};

	/**
	 * Reports the alt value of the current node.
	 * 
	 * @return Alt value as an integer
	 */
	int getAlt();

	void setAlt(int alt);

	/**
	 * Reports the node type of the current node.
	 * 
	 * @return int representing the non-terminal for the current node
	 */
	NodeType getNodeType();

	void setNodeType(NodeType type);

	/**
	 * Reports the number of children of the current node.
	 * 
	 * @return number of children
	 */
	int getChildCount();

	/**
	 * Reports whether the current node has a parent or not.
	 * 
	 * @return whether it has a parent
	 */
	boolean hasParent();

	/**
	 * Add a child to the current node in the parse tree.
	 */
	void addChild();

	/**
	 * Moves the cursor to the parent of the current node.
	 */
	void moveToParent();

	/**
	 * Moves the cursor to the appropriate child of the current node.
	 * 
	 * @param index
	 */
	void moveToChild(int index);

	/**
	 * Reports the string value of the current node if it's an ID node.
	 * 
	 * @return string value of the id
	 */
	String getIdString();

	void setIdString(String id);

	/**
	 * Sets the integer value of the current node if it's an ID node.
	 * 
	 * @param value integer value to be set
	 */
	void setValue(int value);

	/**
	 * Reports the integer value of the current node if it's an ID node.
	 * 
	 * @return integer value of the ID
	 */
	int getValue();

	boolean isDeclared(String id);

	boolean isInitialized(String id);
}