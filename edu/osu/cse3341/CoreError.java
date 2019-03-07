package edu.osu.cse3341;

public class CoreError {
    @SuppressWarnings("serial")
    static class InterpreterException extends Exception {
        public InterpreterException(String msg) {
            super(msg);
        }
    }

    @SuppressWarnings("serial")
    static class InvalidCmdLineArgsException extends InterpreterException {
        public InvalidCmdLineArgsException(String msg) {
            super(msg);
        }
    }

    @SuppressWarnings("serial")
    static class InvalidTokenException extends InterpreterException {
        public InvalidTokenException(String msg) {
            super(msg);
        }
    }

    @SuppressWarnings("serial")
    static class IdRedeclException extends InterpreterException {
        public IdRedeclException(String msg) {
            super(msg);
        }
    }

    @SuppressWarnings("serial")
    static class IdUndeclException extends InterpreterException {
        public IdUndeclException(String msg) {
            super(msg);
        }
    }

    @SuppressWarnings("serial")
    static class InvalidInputException extends InterpreterException {
        public InvalidInputException(String msg) {
            super(msg);
        }
    }

    @SuppressWarnings("serial")
    static class UnderflowOverflowException extends InterpreterException {
        public UnderflowOverflowException(String msg) {
            super(msg);
        }
    }

    @SuppressWarnings("serial")
    static class UnexpectedTokenException extends InterpreterException {
        public UnexpectedTokenException(String msg) {
            super(msg);
        }
    }

    @SuppressWarnings("serial")
    static class UninitializedException extends InterpreterException {
        public UninitializedException(String msg) {
            super(msg);
        }
    }
}
