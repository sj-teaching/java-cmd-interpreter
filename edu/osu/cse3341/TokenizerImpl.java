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
	 * Set of white space characters.
	 */
	private final Set<Character> WHITESPACES;

	/**
	 * Reserved words in the CORE language.
	 */
	private final List<String> RESERVED_WORDS;

	/**
	 * Special symbols in the CORE language.
	 */
	private final List<String> SPECIAL_SYMBOLS;

	/**
	 * Keeps track of the current line number.
	 */
	private int line;

	/**
	 * File to be scanned.
	 */
	private FileReader inputStream;

	/**
	 * Current token.
	 */
	private Token token;

	/**
	 * Current character from the input stream.
	 */
	private Character ch;

	TokenizerImpl(String filePath) throws InvalidTokenException {
		try {
			inputStream = new FileReader(filePath);
		} catch (FileNotFoundException e) {
			System.err.println("File " + filePath + " not found");
			e.printStackTrace();
		}

		WHITESPACES = new HashSet<>(Arrays.asList(' ', '\t', '\r'));

		RESERVED_WORDS = Arrays.asList("program", "begin", "end", "int", //
				"if", "then", "else", "while", "loop", "read", "write", //
				"and", "or");

		SPECIAL_SYMBOLS = Arrays.asList(";", ",", "=", "!", "[", "]", "(", //
				")", "+", "-", "*", "!=", "==", ">=", "<=", ">", "<");

		this.line = 1;

		this.ch = nextChar();
		this.nextToken();
	}

	@Override
	public Token currentToken() {
		return this.token;
	}

	@Override
	public void nextToken() throws InvalidTokenException {
		if (ch == null) {
			this.token = new Token("", this.line, Token.Type.EOF);
			return;
		}

		while (WHITESPACES.contains(ch)) {
			ch = nextChar();
			// ignore
		}

		while (ch == '\n') {
			this.line++;
			ch = nextChar();
		}

		StringBuilder sb = new StringBuilder();

		if (Character.isLowerCase(ch)) {
			while (ch != null && Character.isLowerCase(ch)) {
				sb.append(ch);
				ch = nextChar();
			}
			String tok = sb.toString();
			int code = RESERVED_WORDS.indexOf(tok) + 1; // add 1 for index offset
			if (code > 0) {
				// valid token
				this.token = new Token(tok, this.line, code);
			} else {
				raiseError("Invalid reserved word " + tok);
			}
		} else if (Character.isUpperCase(ch)) {
			while (ch != null && (Character.isUpperCase(ch) || Character.isDigit(ch))) {
				sb.append(ch);
				ch = nextChar();
			}
			String tok = sb.toString();
			if (tok.matches("[A-Z][A-Z]*[0-9]*") && tok.length() <= 8) {
				this.token = new Token(tok, this.line, Token.Type.ID); // Identifier
			} else {
				raiseError("Invalid identifier " + tok);
			}
		} else if (Character.isDigit(ch)) {
			while (ch != null && Character.isDigit(ch)) {
				sb.append(ch);
				ch = nextChar();
			}
			String tok = sb.toString();
			if (tok.matches("[0-9][0-9]*") && tok.length() <= 8) {
				this.token = new Token(tok, this.line, Token.Type.INT);
			} else {
				raiseError("Invalid numeric constant " + tok);
			}
		} else {
			if (ch == '!') {
				Character next = nextChar();
				if (next == '=') {
					this.token = new Token("!=", this.line, Token.Type.NEQ);
					ch = nextChar();
				} else {
					this.token = new Token("!", this.line, Token.Type.NOT);
					ch = next;
				}
			} else if (ch == '>') {
				Character next = nextChar();
				if (next == '=') {
					this.token = new Token(">=", this.line, Token.Type.GEQ);
					ch = nextChar();
				} else {
					this.token = new Token("<=", this.line, Token.Type.GT);
					ch = next;
				}
			} else if (ch == '<') {
				Character next = nextChar();
				if (next == '=') {
					this.token = new Token("<=", this.line, Token.Type.LEQ);
					ch = nextChar();
				} else {
					this.token = new Token("<", this.line, Token.Type.LT);
					ch = next;
				}
			} else if (ch == '=') {
				Character next = nextChar();
				if (next == '=') {
					this.token = new Token("==", this.line, Token.Type.EQ);
					ch = nextChar();
				} else {
					this.token = new Token("==", this.line, Token.Type.ASSIGN);
					ch = next;
				}
			} else {
				String tok = "" + ch;
				int code = SPECIAL_SYMBOLS.indexOf(tok);

				if (code < 0) {
					raiseError("Invalid symbol " + tok);
				} else {
					this.token = new Token(tok, this.line, 14 + code); // add 14 for the offset
				}
				ch = nextChar();
			}
		}

	}

	private void raiseError(String msg) throws InvalidTokenException {
		String excMsg = "Invalid Token: [Line " + this.line + "] " + msg;
		Helper.log.info(excMsg);
		throw new InvalidTokenException(excMsg);
	}

	private Character nextChar() {
		Character character = null;
		try {
			int c = this.inputStream.read();
			if (c != -1) {
				character = (char) c;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return character;
	}
}
