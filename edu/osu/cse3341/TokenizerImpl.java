package edu.osu.cse3341;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenizerImpl implements Tokenizer {

	/**
	 * List of tokens from the language.
	 */
	private List<String> tokens;

	/**
	 * The token stream to be generated.
	 */
	private List<String> tokenStream;

	/**
	 * Index of the current token in the {@code tokenStream}.
	 */
	private int currentIndex;

	/**
	 * Placeholders for the current character on the input stream.
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
	public String currentToken() {
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
	public int code(String token) {
		char first = token.charAt(0);
		int retval;

		if (token.equals("~EOF~")) {
			retval = tokens.indexOf(token);
		} else if (Character.isUpperCase(first)) {
			// it must be an identifier
			retval = tokens.indexOf("ID");
		} else if (Character.isDigit(first)) {
			// it must be a number
			retval = tokens.indexOf("INT");
		} else {
			// it should be either a reserved word or special symbol
			retval = tokens.indexOf(token);
		}

		return retval;
	}

	@Override
	public void tokenize(String filePath) throws InvalidTokenException {
		FileReader inputStream;
		Set<Character> whiteSpaces = new HashSet<>(Arrays.asList(' ', '\t', '\r'));

		try {
			inputStream = new FileReader(filePath);

			current = nextChar(inputStream);
			while (current != null) {
				if (whiteSpaces.contains(current)) {
					current = nextChar(inputStream);
					// ignore
				} else if (current == '\n') {
					lineNum++;
					current = nextChar(inputStream);
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

					tokenStream.add(tok);

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
					tokenStream.add(tok);

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
					tokenStream.add(tok);
				} else {
					if (current == '!') {
						Character next = nextChar(inputStream);
						if (next == '=') {
							tokenStream.add("!=");
							current = nextChar(inputStream);
						} else {
							tokenStream.add("!");
							current = next;
						}
					} else if (current == '>') {
						Character next = nextChar(inputStream);
						if (next == '=') {
							tokenStream.add(">=");
							current = nextChar(inputStream);
						} else {
							tokenStream.add(">");
							current = next;
						}
					} else if (current == '<') {
						Character next = nextChar(inputStream);
						if (next == '=') {
							tokenStream.add("<=");
							current = nextChar(inputStream);
						} else {
							tokenStream.add("<");
							current = next;
						}
					} else if (current == '=') {
						Character next = nextChar(inputStream);
						if (next == '=') {
							tokenStream.add("==");
							current = nextChar(inputStream);
						} else {
							tokenStream.add("=");
							current = next;
						}
					} else {
						String tok = "" + current;
						if (tokens.indexOf(tok) < 0) {
							raiseError("Invalid symbols " + tok);
						}
						tokenStream.add(tok);
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
			tokenStream.add("~EOF~");
		}
	}

	private void raiseError(String msg) throws InvalidTokenException {
		String excMsg="Invalid Token: [Line " + lineNum + "] " + msg;
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

	@Override
	public String getTokenStream() {
		return tokenStream.toString();
	}
}
