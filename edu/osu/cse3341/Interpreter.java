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
		assert pt.getNodeType() == NodeType.EXP : "Node must be a TERM";

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
//	printCompOp(ParseTree)
//	printComp(ParseTree)
//	printCond(ParseTree)
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
