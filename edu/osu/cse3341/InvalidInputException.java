package edu.osu.cse3341;

@SuppressWarnings("serial")
public class InvalidInputException extends InterpreterException {
	public InvalidInputException(String msg) {
		super(msg);
	}
}