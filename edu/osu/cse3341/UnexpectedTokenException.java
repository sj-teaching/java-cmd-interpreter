package edu.osu.cse3341;

@SuppressWarnings("serial")
public class UnexpectedTokenException extends InterpreterException {
	public UnexpectedTokenException(String msg) {
		super(msg);
	}
}
