package edu.osu.cse3341;

@SuppressWarnings("serial")
public class InvalidTokenException extends InterpreterException {
	public InvalidTokenException(String msg) {
		super(msg);
	}
}
