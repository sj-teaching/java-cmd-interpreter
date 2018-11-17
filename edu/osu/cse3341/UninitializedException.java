package edu.osu.cse3341;

@SuppressWarnings("serial")
public class UninitializedException extends InterpreterException {
	public UninitializedException(String msg) {
		super(msg);
	}
}