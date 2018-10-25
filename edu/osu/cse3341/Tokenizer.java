package edu.osu.cse3341;

public interface Tokenizer {
	public enum TokenType {
		PROGRAM, BEGIN, END, INT, IF, THEN, ELSE, WHILE, LOOP, READ, WRITE, AND, OR, SEMICOL, COMMA, ASSIGN, NOT,
		LBRACK, RBRACK, LPAREN, RPAREN, PLUS, MINUS, STAR, NEQ, EQ, GEQ, LEQ, GT, LT, NUM, ID, EOF;
	}

	/**
	 * Returns the current token from the stream.
	 *
	 * @ensures currentToken = [current token]
	 * @return current token as a String
	 */
	String currentToken();

	/**
	 * Advances to the next token in the stream.
	 *
	 * @requires [Another token exists on the stream]
	 */
	void nextToken();

	/**
	 * Reports if another token exists on the stream.
	 *
	 * @ensures hasNext = [another token exists on the stream]
	 * @return whether there is another token on the stream
	 */
	boolean hasNext();

	/**
	 * Loads the file at the given path and generates the token stream.
	 *
	 * @requires [A file exists at {@code filepath}]
	 * @param filepath The path of the file to tokenize
	 */
	void tokenize(String filepath);

	/**
	 * Returns the integer code for the given token.
	 *
	 * @requires [{@code token} is a valid token]
	 * @param token The token whose integer code is asked
	 * @return Integer code for the given token
	 */
	int code(String token);

	/**
	 * Returns the token stream in a {@code String} format.
	 * 
	 * @return String representation of the {@code tokenStream}
	 */
	String getTokenStream();
}
