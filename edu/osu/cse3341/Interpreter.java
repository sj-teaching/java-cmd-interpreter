package edu.osu.cse3341;

import edu.osu.cse3341.ParseTree.NodeType;

public class Interpreter {

	public static int evalFac(ParseTree pt) {
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

	public static int evalTerm(ParseTree pt) {
		assert pt.getNodeType() == NodeType.TERM : "Node must be a TERM";

		int alt = pt.getAlt();

		pt.moveToChild(0);
		int val = evalFac(pt);
		pt.moveToParent();

		if (alt == 2) {
			pt.moveToChild(1);
			val = val * evalTerm(pt);
			pt.moveToParent();
		}

		return val;
	}

	public static int evalExp(ParseTree pt) {
		assert pt.getNodeType() == NodeType.EXP : "Node must be an EXP";

		int alt = pt.getAlt();

		pt.moveToChild(0);
		int val = evalTerm(pt);
		pt.moveToParent();

		if (alt == 2) {
			pt.moveToChild(1);
			val = val + evalExp(pt);
			pt.moveToParent();
		} else if (alt == 3) {
			pt.moveToChild(1);
			val = val - evalExp(pt);
			pt.moveToParent();
		}
		return val;
	}

	public static boolean evalComp(ParseTree pt) {
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

	public static boolean evalCond(ParseTree pt) {
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
//	printIdList(ParseTree)
//	printIn(ParseTree)
//	printOut(ParseTree)
//	printAssign(ParseTree)
//	printLoop(ParseTree)
//	printIf(ParseTree)
//	printStmt(ParseTree)
//	printSS(ParseTree)
//	printDecl(ParseTree)
//	printDS(ParseTree)
//	printProg(ParseTree)
}
