package edu.osu.cse3341;

import java.util.InputMismatchException;
import java.util.Scanner;

import edu.osu.cse3341.ParseTree.NodeType;

public class Interpreter {

	private static final int RADIX = 10;
	private static Scanner keyboard = new Scanner(System.in);

	public static int evalFac(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.FAC : "Node must be a FAC";

		int val = 0, alt = pt.getAlt();
		pt.moveToChild(0);

		if (alt == 1 || alt == 2) {
			// <int> | <id>
			val = pt.getValue();
		} else {
			// ( <exp> )
			val = evalExp(pt);
		}
		pt.moveToParent();
		return val;
	}

	public static int evalTerm(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.TERM : "Node must be a TERM";

		int alt = pt.getAlt();
		pt.moveToChild(0);
		int val = evalFac(pt);
		pt.moveToParent();

		if (alt == 2) {
			pt.moveToChild(1);
			int op2 = evalTerm(pt);
			try {
				val = Math.multiplyExact(val, op2);
			} catch (ArithmeticException e) {
				throw new UnderflowOverflowException(
						"Multiplication of " + val + " and " + op2 + " results in an underflow or overflow.");
			}
			pt.moveToParent();
		}

		return val;
	}

	public static int evalExp(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.EXP : "Node must be an EXP";

		int alt = pt.getAlt();

		pt.moveToChild(0);
		int val = evalTerm(pt);
		pt.moveToParent();

		if (alt == 2) {
			pt.moveToChild(1);
			int op2 = evalExp(pt);
			try {
				val = Math.addExact(val, op2);
			} catch (ArithmeticException e) {
				throw new UnderflowOverflowException(
						"Multiplication of " + val + " and " + op2 + " results in an underflow or overflow.");
			}
			pt.moveToParent();
		} else if (alt == 3) {
			pt.moveToChild(1);
			int op2 = evalExp(pt);
			try {
				val = Math.subtractExact(val, op2);
			} catch (ArithmeticException e) {
				throw new UnderflowOverflowException(
						"Multiplication of " + val + " and " + op2 + " results in an underflow or overflow.");
			}
			pt.moveToParent();
		}
		return val;
	}

	public static boolean evalComp(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.COMP : "Node must be a COMP";

		pt.moveToChild(0);
		int fac1 = evalFac(pt);
		pt.moveToParent();

		pt.moveToChild(1);
		int compOp = pt.getAlt();
		pt.moveToParent();

		pt.moveToChild(2);
		int fac2 = evalFac(pt);
		pt.moveToParent();

		boolean val = false;
		if (compOp == 1) {// !=
			val = fac1 != fac2;
		} else if (compOp == 2) { // ==
			val = fac1 == fac2;
		} else if (compOp == 3) { // <
			val = fac1 < fac2;
		} else if (compOp == 4) { // >
			val = fac1 > fac2;
		} else if (compOp == 5) { // <=
			val = fac1 <= fac2;
		} else { // >=
			val = fac1 >= fac2;
		}

		return val;
	}

	public static boolean evalCond(ParseTree pt) throws InterpreterException {
		// <cond> ::= <comp>
		// | !<cond>
		// | [ <cond> and <cond> ]
		// | [ <cond> or <cond> ]

		assert pt.getNodeType() == NodeType.COND : "Node must be a COND";
		boolean val = false;
		int alt = pt.getAlt();

		if (alt == 1) {
			pt.moveToChild(0);
			val = evalComp(pt);
			pt.moveToParent();
		} else {
			pt.moveToChild(0);
			boolean cond1 = evalCond(pt);
			pt.moveToParent();
			if (alt == 2) {
				val = !cond1;
			} else {
				pt.moveToChild(1);
				boolean cond2 = evalCond(pt);
				pt.moveToParent();
				if (alt == 3) {
					val = cond1 && cond2;
				} else {
					val = cond1 || cond2;
				}
			}
		}
		return val;
	}

	public static void readIdList(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.IDLIST : "Node must be an IDLIST";

		pt.moveToChild(0);
		String id = pt.getIdString();
		System.out.print(id + " =? ");

		int val = 0;
		try {
			val = keyboard.nextInt(RADIX);
		} catch (InputMismatchException e) {
			throw new InvalidInputException("User must enter a valid inetger.");
		}

		pt.setValue(val);
		pt.moveToParent();

		if (pt.getAlt() == 2) {
			pt.moveToChild(1);
			readIdList(pt);
			pt.moveToParent();
		}
	}

	public static void writeIdList(ParseTree pt) {
		assert pt.getNodeType() == NodeType.IDLIST : "Node must be an IDLIST";

		pt.moveToChild(0);
		String id = pt.getIdString();
		System.out.println(id + " = " + pt.getValue());
		pt.moveToParent();

		if (pt.getAlt() == 2) {
			pt.moveToChild(1);
			writeIdList(pt);
			pt.moveToParent();
		}
	}

	public static void execIn(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.IN : "Node must be an IN";

		pt.moveToChild(0);
		readIdList(pt);
		pt.moveToParent();
	}

	public static void execOut(ParseTree pt) {
		assert pt.getNodeType() == NodeType.OUT : "Node must be an OUT";

		pt.moveToChild(0);
		writeIdList(pt);
		pt.moveToParent();
	}

	public static void execAssign(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.ASSIGN : "Node must be an ASSIGN";

		pt.moveToChild(1);
		int val = evalExp(pt);
		pt.moveToParent();

		pt.moveToChild(0);
		pt.setValue(val);
		pt.moveToParent();
	}

	public static void execIf(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.IF : "Node must be an IF";

		pt.moveToChild(0);
		boolean cond = evalCond(pt);
		pt.moveToParent();

		if (cond) {
			pt.moveToChild(1);
			execSS(pt);
			pt.moveToParent();
		} else if (pt.getAlt() == 2) {
			pt.moveToChild(2);
			execSS(pt);
			pt.moveToParent();
		}
	}

	public static void execLoop(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.LOOP : "Node must be a LOOP";

		pt.moveToChild(0);
		boolean cond = evalCond(pt);
		pt.moveToParent();

		while (cond) {
			pt.moveToChild(1);
			execSS(pt);
			pt.moveToParent();

			pt.moveToChild(0);
			cond = evalCond(pt);
			pt.moveToParent();
		}
	}

	public static void execStmt(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.STMT : "Node must be a STMT";

		// <stmt> ::= <assign>
		// | <if>
		// | <loop>
		// | <in>
		// | <out>

		int alt = pt.getAlt();
		pt.moveToChild(0);
		if (alt == 1) {
			execAssign(pt);
		} else if (alt == 2) {
			execIf(pt);
		} else if (alt == 3) {
			execLoop(pt);
		} else if (alt == 4) {
			execIn(pt);
		} else {
			execOut(pt);
		}
		pt.moveToParent();
	}

	public static void execSS(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.SS : "Node must be a SS";

		pt.moveToChild(0);
		execStmt(pt);
		pt.moveToParent();

		if (pt.getAlt() == 2) {
			pt.moveToChild(1);
			execSS(pt);
			pt.moveToParent();
		}
	}

	public static void execProg(ParseTree pt) throws InterpreterException {
		assert pt.getNodeType() == NodeType.PROG : "Node must be a PROG";

		pt.moveToChild(1);
		execSS(pt);
		pt.moveToParent();
	}
}
