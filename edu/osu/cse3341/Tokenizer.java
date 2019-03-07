package edu.osu.cse3341;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.osu.cse3341.Main;

public class Tokenizer {
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
    private int line;

    Tokenizer() {
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
        line = 1;
    }

    /**
     * Returns the current token from the stream.
     *
     * @ensures currentToken = [current token]
     * @return current token as a String
     */
    public Token currentToken() {
        return tokenStream.get(currentIndex);
    }

    /**
     * Advances to the next token in the stream.
     *
     * @throws CoreError.InvalidTokenException
     *
     * @requires [Another token exists on the stream]
     */
    public void nextToken() {
        assert hasNext() : "Violates there is another token on the stream.";
        currentIndex++;
    }

    public boolean hasNext() {
        return currentIndex < tokenStream.size();
    }

    public void tokenize(String filePath) throws CoreError.InvalidTokenException {
        FileReader inputStream;

        try {
            inputStream = new FileReader(filePath);

            current = nextChar(inputStream);
            while (current != null) {
                if (current == '\n') {
                    line++;
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
                        Main.raiseTokError("[Line "+line+"] Invalid reserved word " + tok);
                    }

                    tokenStream.add(new Token(tok, line, tokens.indexOf(tok)));

                } else if (Character.isUpperCase(current)) {
                    // See if it's an identifier
                    StringBuilder sb = new StringBuilder();
                    while (current != null && Character.isLetterOrDigit(current)) {
                        sb.append(current);
                        current = nextChar(inputStream);
                    }

                    String tok = sb.toString();
                    if (!tok.matches("[A-Z][A-Z]*[0-9]*") || tok.length() > 8) {
                        Main.raiseTokError("[Line "+line+"] Invalid identifier " + tok);
                    }
                    tokenStream.add(new Token(tok, line, Token.ID));

                } else if (Character.isDigit(current)) {
                    // See if it's a number
                    StringBuilder sb = new StringBuilder();
                    while (current != null && Character.isLetterOrDigit(current)) {
                        sb.append(current);
                        current = nextChar(inputStream);
                    }

                    String tok = sb.toString();
                    if (!tok.matches("[0-9][0-9]*") || tok.length() > 8) {
                        Main.raiseTokError("[Line "+line+"] Invalid numeric constant " + tok);
                    }
                    tokenStream.add(new Token(tok, line, Token.NUM));
                } else {
                    if (current == '!') {
                        Character next = nextChar(inputStream);
                        if (next == '=') {
                            tokenStream.add(new Token("!=", line, Token.NEQ));
                            current = nextChar(inputStream);
                        } else {
                            tokenStream.add(new Token("!", line, Token.NOT));
                            current = next;
                        }
                    } else if (current == '>') {
                        Character next = nextChar(inputStream);
                        if (next == '=') {
                            tokenStream.add(new Token(">=", line, Token.GEQ));
                            current = nextChar(inputStream);
                        } else {
                            tokenStream.add(new Token(">", line, Token.GT));
                            current = next;
                        }
                    } else if (current == '<') {
                        Character next = nextChar(inputStream);
                        if (next == '=') {
                            tokenStream.add(new Token("<=", line, Token.LEQ));
                            current = nextChar(inputStream);
                        } else {
                            tokenStream.add(new Token("<", line, Token.LT));
                            current = next;
                        }
                    } else if (current == '=') {
                        Character next = nextChar(inputStream);
                        if (next == '=') {
                            tokenStream.add(new Token("==", line, Token.EQ));
                            current = nextChar(inputStream);
                        } else {
                            tokenStream.add(new Token("=", line, Token.ASSIGN));
                            current = next;
                        }
                    } else {
                        String tok = "" + current;
                        if (tokens.indexOf(tok) < 0) {
                            Main.raiseTokError("[Line "+line+"] Invalid symbols " + tok);
                        }
                        tokenStream.add(new Token(tok, line, tokens.indexOf(tok)));
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
            tokenStream.add(new Token("~EOF~", line, Token.EOF));
        }
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
