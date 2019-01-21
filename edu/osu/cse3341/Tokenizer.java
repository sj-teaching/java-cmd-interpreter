package edu.osu.cse3341;

public interface Tokenizer {

	/**
	 * Returns the current token from the stream.
	 *
	 * @ensures currentToken = [current token]
	 * @return current token as a String
	 */
	Token currentToken();

	/**
	 * Advances to the next token in the stream.
	 * 
	 * @throws InvalidTokenException
	 *
	 * @requires [Another token exists on the stream]
	 */
	void nextToken() throws InvalidTokenException;

	boolean hasNext();

	void tokenize(String filePath) throws InvalidTokenException;
}
