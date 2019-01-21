package edu.osu.cse3341;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenizerImpl implements Tokenizer {

	/**
	 * List of tokens from the language.
	 */
	private List<String> tokens;

	/**
	 * The token stream to be generated.
	 */
	private List<Token> tokenStream;

	/**
	 * Index of the current token in the {@code tokenStream}.
	 */
	private int currentIndex;

	/**
	 * Placeholder for the current character on the input stream.
	 */
	private Character current;

	/**
	 * Keeps track of the current line number.
	 */
	private int lineNum;

	TokenizerImpl() {
		// Build the list of tokens from the language
		tokens = Arrays.asList(
				// placeholder for index zero
				"",
				// reserved words
				"program", "begin", "end", "int", "if", "then", "else", "while", "loop", "read", "write", "and", "or",

				// special symbols
				";", ",", "=", "!", "[", "]", "(", ")", "+", "-", "*", "!=", "==", ">=", "<=", ">", "<",

				// integers
				"INT",

				// identifier
				"ID",

				// end-of-file
				"~EOF~");

		// Initialize the private fields
		tokenStream = new ArrayList<>();
		currentIndex = 0;
		current = null;
		lineNum = 1;
	}

	@Override
	public Token currentToken() {
		return tokenStream.get(currentIndex);
	}

	@Override
	public void nextToken() {
		assert hasNext() : "Violates there is another token on the stream.";
		currentIndex++;
	}

	@Override
	public boolean hasNext() {
		return currentIndex < tokenStream.size();
	}

	@Override
	public void tokenize(String filePath) throws InvalidTokenException {
		FileReader inputStream;

		try {
			inputStream = new FileReader(filePath);

			current = nextChar(inputStream);
			while (current != null) {
				if (current == '\n') {
					lineNum++;
					current = nextChar(inputStream);
				} else if (Character.isWhitespace(current)) {
					current = nextChar(inputStream);
					// ignore
				} else if (Character.isLowerCase(current)) {
					// See if it's a reserved word
					StringBuilder sb = new StringBuilder();
					while (current != null && Character.isLetterOrDigit(current)) {
						sb.append(current);
						current = nextChar(inputStream);
					}

					String tok = sb.toString();
					if (tokens.indexOf(tok) < 0) {
						// tok is not in tokens, i.e. these lowercase characters don't match a keyword
						raiseError("Invalid reserved word " + tok);
					}

					tokenStream.add(new Token(tok, lineNum, tokens.indexOf(tok)));

				} else if (Character.isUpperCase(current)) {
					// See if it's an identifier
					StringBuilder sb = new StringBuilder();
					while (current != null && Character.isLetterOrDigit(current)) {
						sb.append(current);
						current = nextChar(inputStream);
					}

					String tok = sb.toString();
					if (!tok.matches("[A-Z][A-Z]*[0-9]*") || tok.length() > 8) {
						raiseError("Invalid identifier " + tok);
					}
					tokenStream.add(new Token(tok, lineNum, Token.ID));

				} else if (Character.isDigit(current)) {
					// See if it's a number
					StringBuilder sb = new StringBuilder();
					while (current != null && Character.isLetterOrDigit(current)) {
						sb.append(current);
						current = nextChar(inputStream);
					}

					String tok = sb.toString();
					if (!tok.matches("[0-9][0-9]*") || tok.length() > 8) {
						raiseError("Invalid numeric constant " + tok);
					}
					tokenStream.add(new Token(tok, lineNum, Token.NUM));
				} else {
					if (current == '!') {
						Character next = nextChar(inputStream);
						if (next == '=') {
							tokenStream.add(new Token("!=", lineNum, Token.NEQ));
							current = nextChar(inputStream);
						} else {
							tokenStream.add(new Token("!", lineNum, Token.NOT));
							current = next;
						}
					} else if (current == '>') {
						Character next = nextChar(inputStream);
						if (next == '=') {
							tokenStream.add(new Token(">=", lineNum, Token.GEQ));
							current = nextChar(inputStream);
						} else {
							tokenStream.add(new Token(">", lineNum, Token.GT));
							current = next;
						}
					} else if (current == '<') {
						Character next = nextChar(inputStream);
						if (next == '=') {
							tokenStream.add(new Token("<=", lineNum, Token.LEQ));
							current = nextChar(inputStream);
						} else {
							tokenStream.add(new Token("<", lineNum, Token.LT));
							current = next;
						}
					} else if (current == '=') {
						Character next = nextChar(inputStream);
						if (next == '=') {
							tokenStream.add(new Token("==", lineNum, Token.EQ));
							current = nextChar(inputStream);
						} else {
							tokenStream.add(new Token("=", lineNum, Token.ASSIGN));
							current = next;
						}
					} else {
						String tok = "" + current;
						if (tokens.indexOf(tok) < 0) {
							raiseError("Invalid symbols " + tok);
						}
						tokenStream.add(new Token(tok, lineNum, tokens.indexOf(tok)));
						current = nextChar(inputStream);
					}
				}
			}

			inputStream.close();
		} catch (FileNotFoundException e) {
			System.err.println("File " + filePath + " not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// add the special ~EOF~ symbol at the end
			tokenStream.add(new Token("~EOF~", lineNum, Token.EOF));
		}
	}

	private void raiseError(String msg) throws InvalidTokenException {
		String excMsg = "Invalid Token: [Line " + lineNum + "] " + msg;
		Helper.log.info(excMsg);
		throw new InvalidTokenException(excMsg);
	}

	private Character nextChar(FileReader inputStream) {
		Character character = null;
		try {
			int c = inputStream.read();
			if (c != -1) {
				character = (char) c;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return character;
	}
}
