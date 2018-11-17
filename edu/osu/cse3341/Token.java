package edu.osu.cse3341;

public class Token {
	private String name;
	private Tokenizer.TokenType type;
	private int line;

	public Token(String name, int line) {
		this.name = name;
		this.line = line;
		// TODO How to infer type?
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Tokenizer.TokenType getType() {
		return type;
	}

	public void setType(Tokenizer.TokenType type) {
		this.type = type;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

}
