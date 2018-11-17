package edu.osu.cse3341;

@SuppressWarnings("serial")
public class UnderflowOverflowException extends InterpreterException {
	public UnderflowOverflowException(String msg) {
		super(msg);
	}
}