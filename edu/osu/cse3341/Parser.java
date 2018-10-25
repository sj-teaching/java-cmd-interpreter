package edu.osu.cse3341;

import edu.osu.cse3341.ParseTree.NodeType;

public class Parser {
	static Tokenizer t;

	private static final int OFFSET = 2;
	private static int indentLevel = 0;

	private Parser() {
		/* cannot be instantiated - use all static methods */
	}

	public static ParseTree parse(String filepath) throws UnexpectedTokenException, InvalidTokenException {
		t = new TokenizerImpl();
		t.tokenize(filepath);
		Helper.log.info(t.getTokenStream());

		ParseTree pt = new ParseTreeImpl();
		parseProg(pt);
		matchConsume("~EOF~");
		return pt;
	}

	public static void print(ParseTree pt) {
		printProg(pt);
	}

	// --- Helper Methods ---
	static void matchConsume(String exptok) throws UnexpectedTokenException {
		String actual = t.currentToken();
		if ((exptok.equals("INT") && t.code(actual) != 31) // int code
				|| (exptok.equals("ID") && t.code(actual) != 32) // id code
				|| (exptok.equals("~EOF~") && t.code(actual) != 33) // eof code
				|| (!exptok.equals("INT") && !exptok.equals("ID") && !exptok.equals("~EOF~")
						&& !exptok.equals(actual))) {
			Helper.log.info("Syntax Error! Expected: " + exptok + ", found: " + actual);
			throw new UnexpectedTokenException(
					"Syntax Error: [Line " + t.currentTokenLine() + "] Expected: " + exptok + ", found: " + actual);
		}
		t.nextToken();
		Helper.log.info("Consumed: " + actual);
	}

	static void indent() {
		for (int i = 0; i < indentLevel * OFFSET; i++) {
			System.out.print(' ');
		}
	}

	private static void print(String string) {
		System.out.print(string);
	}

	// --- Parse Methods ---

	static void parseInt(ParseTree pt) throws UnexpectedTokenException {
		pt.setAlt(1);
		pt.setNodeType(NodeType.INT);
		int val = Integer.parseInt(t.currentToken());
		pt.setValue(val);
		matchConsume("INT");
	}

	static void parseId(ParseTree pt) {
		pt.setNodeType(NodeType.ID);
		String id = t.currentToken();
		t.nextToken();
		Helper.log.info("Consumed: " + id);

		pt.setIdString(id);
	}

	static void parseFac(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.FAC);

		String tok = t.currentToken();
		if (t.code(tok) == 31) {// INT
			pt.setAlt(1);
			pt.addChild();
			pt.moveToChild(0);
			parseInt(pt);
			pt.moveToParent();
		} else if (t.code(tok) == 32) { // ID
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(0);
			parseId(pt);
			pt.moveToParent();
		} else if (tok.equals("(")) {
			matchConsume("(");
			pt.setAlt(3);
			pt.addChild();
			pt.moveToChild(0);
			parseExp(pt);
			pt.moveToParent();
			matchConsume(")");
		} else {
			throw new UnexpectedTokenException(
					"[Line " + t.currentTokenLine() + "] Unexpected token " + tok + " while parsing <fac>.");
		}
	}

	static void parseTerm(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.TERM);
		pt.addChild();
		pt.moveToChild(0);
		parseFac(pt);
		pt.moveToParent();

		if (t.currentToken().equals("*")) {
			matchConsume("*");
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(1);
			parseTerm(pt);
			pt.moveToParent();
		} else {
			pt.setAlt(1);
		}
	}

	static void parseExp(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.EXP);
		pt.addChild();
		pt.moveToChild(0);
		parseTerm(pt);
		pt.moveToParent();

		String tok = t.currentToken();
		if (tok.equals("+")) {
			matchConsume("+");
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(1);
			parseExp(pt);
			pt.moveToParent();
		} else if (tok.equals("-")) {
			matchConsume("-");
			pt.setAlt(3);
			pt.addChild();
			pt.moveToChild(1);
			parseExp(pt);
			pt.moveToParent();
		} else {
			pt.setAlt(1);
		}
	}

	static void parseCompOp(ParseTree pt) {
		pt.setNodeType(NodeType.COMPOP);

		String tok = t.currentToken();
		if (tok.equals("!=")) {
			pt.setAlt(1);
		} else if (tok.equals("==")) {
			pt.setAlt(2);
		} else if (tok.equals("<")) {
			pt.setAlt(3);
		} else if (tok.equals(">")) {
			pt.setAlt(4);
		} else if (tok.equals("<=")) {
			pt.setAlt(5);
		} else if (tok.equals(">=")) {
			pt.setAlt(6);
		}
		t.nextToken();
		Helper.log.info("Consumed " + tok);
	}

	static void parseComp(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.COMP);
		matchConsume("(");
		pt.addChild();
		pt.moveToChild(0);
		parseFac(pt);
		pt.moveToParent();

		pt.addChild();
		pt.moveToChild(1);
		parseCompOp(pt);
		pt.moveToParent();

		pt.addChild();
		pt.moveToChild(2);
		parseFac(pt);
		pt.moveToParent();

		matchConsume(")");
	}

	static void parseCond(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.COND);

		String tok = t.currentToken();
		if (tok.equals("!")) {
			matchConsume("!");
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(0);
			parseCond(pt);
			pt.moveToParent();
		} else if (tok.equals("[")) {
			matchConsume("[");

			pt.addChild();
			pt.moveToChild(0);
			parseCond(pt);
			pt.moveToParent();

			tok = t.currentToken();
			if (tok.equals("and")) {
				pt.setAlt(3);
				matchConsume("and");
			} else if (tok.equals("or")) {
				pt.setAlt(4);
				matchConsume("or");
			}

			pt.addChild();
			pt.moveToChild(1);
			parseCond(pt);
			pt.moveToParent();

			matchConsume("]");
		} else {
			pt.setAlt(1);
			pt.addChild();
			pt.moveToChild(0);
			parseComp(pt);
			pt.moveToParent();
		}
	}

	static void parseIdList(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.IDLIST);
		pt.addChild();
		pt.moveToChild(0);
		parseId(pt);
		pt.moveToParent();

		String tok = t.currentToken();
		if (tok.equals(",")) {
			matchConsume(",");
			Helper.log.info("Consumed " + tok);

			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(1);
			parseIdList(pt);
			pt.moveToParent();
		} else {
			pt.setAlt(1);
		}
	}

	static void parseIn(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.IN);
		matchConsume("read");
		pt.addChild();
		pt.moveToChild(0);
		parseIdList(pt);
		pt.moveToParent();

		matchConsume(";");
	}

	static void parseOut(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.OUT);
		matchConsume("write");
		pt.addChild();
		pt.moveToChild(0);
		parseIdList(pt);
		pt.moveToParent();

		matchConsume(";");
	}

	static void parseAssign(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.ASSIGN);

		pt.addChild();
		pt.moveToChild(0);
		parseId(pt);
		pt.moveToParent();

		matchConsume("=");

		pt.addChild();
		pt.moveToChild(1);
		parseExp(pt);
		pt.moveToParent();

		matchConsume(";");
	}

	static void parseLoop(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.LOOP);
		matchConsume("while");
		pt.addChild();
		pt.moveToChild(0);
		parseCond(pt);
		pt.moveToParent();

		matchConsume("loop");
		pt.addChild();
		pt.moveToChild(1);
		parseSS(pt);
		pt.moveToParent();

		matchConsume("end");
		matchConsume(";");
	}

	static void parseIf(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.IF);
		pt.setAlt(1);

		matchConsume("if");
		pt.addChild();
		pt.moveToChild(0);
		parseCond(pt);
		pt.moveToParent();

		matchConsume("then");
		pt.addChild();
		pt.moveToChild(1);
		parseSS(pt);
		pt.moveToParent();

		if (t.currentToken().equals("else")) {
			matchConsume("else");
			pt.addChild();
			pt.moveToChild(2);
			parseSS(pt);
			pt.moveToParent();

			pt.setAlt(2);
		}

		matchConsume("end");
		matchConsume(";");
	}

	static void parseStmt(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.STMT);
		String tok = t.currentToken();

		pt.addChild();
		pt.moveToChild(0);

		if (t.code(tok) == 32) {
			parseAssign(pt);
			pt.moveToParent();
			pt.setAlt(1);
		} else if (tok.equals("if")) {
			parseIf(pt);
			pt.moveToParent();
			pt.setAlt(2);
		} else if (tok.equals("while")) {
			parseLoop(pt);
			pt.moveToParent();
			pt.setAlt(3);
		} else if (tok.equals("read")) {
			parseIn(pt);
			pt.moveToParent();
			pt.setAlt(4);
		} else if (tok.equals("write")) {
			parseOut(pt);
			pt.moveToParent();
			pt.setAlt(5);
		} else {
			throw new UnexpectedTokenException(
					"[Line " + t.currentTokenLine() + "] Unexpected token " + tok + " while parsing <stmt>");
		}

	}

	static void parseSS(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.SS);

		pt.addChild();
		pt.moveToChild(0);
		parseStmt(pt);
		pt.moveToParent();

		String tok = t.currentToken();
		// Observation from the grammar: Stmt-Seq's are followed by "end" or "else" only
		if (tok.equals("end") || tok.equals("else")) {
			pt.setAlt(1);
		} else {
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(1);
			parseSS(pt);
			pt.moveToParent();
		}
	}

	static void parseDecl(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.DECL);
		matchConsume("int");
		pt.addChild();
		pt.moveToChild(0);
		parseIdList(pt);
		pt.moveToParent();

		matchConsume(";");
	}

	static void parseDS(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.DS);

		pt.addChild();
		pt.moveToChild(0);
		parseDecl(pt);
		pt.moveToParent();

		if (t.currentToken().equals("int")) {
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(1);
			parseIdList(pt);
			pt.moveToParent();
		}
	}

	static void parseProg(ParseTree pt) throws UnexpectedTokenException {
		pt.setNodeType(NodeType.PROG);
		matchConsume("program");
		pt.addChild();
		pt.moveToChild(0);
		parseDS(pt);
		pt.moveToParent();

		matchConsume("begin");
		pt.addChild();
		pt.moveToChild(1);
		parseSS(pt);
		pt.moveToParent();

		matchConsume("end");
	}

	// --- Print Methods ---
	static void printInt(ParseTree pt) {
		assert pt.getNodeType() == NodeType.INT : "Node must be an INT";
		System.out.print(pt.getValue());
	}

	static void printId(ParseTree pt) {
		assert pt.getNodeType() == NodeType.ID : "Node must be an ID";
		System.out.print(pt.getIdString());
	}

	static void printFac(ParseTree pt) {
		assert pt.getNodeType() == NodeType.FAC : "Node must be a FAC";
		int alt = pt.getAlt();
		pt.moveToChild(0);
		if (alt == 1) {
			// <int>
			printInt(pt);
		} else if (alt == 2) {// <id>
			printId(pt);
		} else {// ( <exp> )
			System.out.print("( ");
			printExp(pt);
			System.out.print(" )");
		}
		pt.moveToParent();
	}

	static void printTerm(ParseTree pt) {
		assert pt.getNodeType() == NodeType.TERM : "Node must be an TERM";
		pt.moveToChild(0);
		printFac(pt);
		pt.moveToParent();

		if (pt.getAlt() == 2) {
			print(" * ");
			pt.moveToChild(1);
			printTerm(pt);
			pt.moveToParent();
		}
	}

	static void printExp(ParseTree pt) {
		assert pt.getNodeType() == NodeType.EXP : "Node must be an EXP";
		pt.moveToChild(0);
		printTerm(pt);
		pt.moveToParent();

		int alt = pt.getAlt();
		if (alt > 1) {
			if (alt == 2) {
				print(" + ");
			} else {
				print(" - ");
			}
			pt.moveToChild(1);
			printExp(pt);
			pt.moveToParent();
		}
	}

	static void printCompOp(ParseTree pt) {
		assert pt.getNodeType() == NodeType.COMPOP : "Node must be a COMPOP";
		String[] compOps = { "", " != ", " == ", " < ", " > ", " <= ", " >= " };
		int alt = pt.getAlt();
		print(compOps[alt]);
	}

	static void printComp(ParseTree pt) {
		assert pt.getNodeType() == NodeType.COMP : "Node must be a COMP";
		print("( ");

		pt.moveToChild(0);
		printFac(pt);
		pt.moveToParent();
		pt.moveToChild(1);
		printCompOp(pt);
		pt.moveToParent();
		pt.moveToChild(2);
		printFac(pt);
		pt.moveToParent();

		print(" )");
	}

	static void printCond(ParseTree pt) {
		assert pt.getNodeType() == NodeType.COND : "Node must be an COND";
		int alt = pt.getAlt();
		if (alt == 1) {
			pt.moveToChild(0);
			printComp(pt);
			pt.moveToParent();
		} else {
			if (alt == 2) {
				print("!");
				pt.moveToChild(0);
				printCond(pt);
				pt.moveToParent();
			} else {
				print("[ ");
				pt.moveToChild(0);
				printCond(pt);
				pt.moveToParent();
				if (alt == 3) {
					print(" and ");
				} else {
					print(" or ");
				}
				pt.moveToChild(1);
				printCond(pt);
				pt.moveToParent();
				print(" ]");
			}
		}
	}

	static void printIdList(ParseTree pt) {
		assert pt.getNodeType() == NodeType.IDLIST : "Node must be an IDLIST";
		pt.moveToChild(0);
		printId(pt);
		pt.moveToParent();
		if (pt.getAlt() == 2) {
			print(", ");
			pt.moveToChild(1);
			printIdList(pt);
			pt.moveToParent();
		}
	}

	static void printIn(ParseTree pt) {
		assert pt.getNodeType() == NodeType.IN : "Node must be an IN";
		print("read ");
		pt.moveToChild(0);
		printIdList(pt);
		pt.moveToParent();
		print(";\n");
	}

	static void printOut(ParseTree pt) {
		assert pt.getNodeType() == NodeType.OUT : "Node must be an OUT";
		print("write ");
		pt.moveToChild(0);
		printIdList(pt);
		pt.moveToParent();
		print(";\n");
	}

	static void printAssign(ParseTree pt) {
		assert pt.getNodeType() == NodeType.ASSIGN : "Node must be an ASSIGN";
		pt.moveToChild(0);
		printId(pt);
		pt.moveToParent();
		print(" = ");
		pt.moveToChild(1);
		printExp(pt);
		pt.moveToParent();
		print(";\n");
	}

	static void printLoop(ParseTree pt) {
		assert pt.getNodeType() == NodeType.LOOP : "Node must be a LOOP";
		print("while ");
		pt.moveToChild(0);
		printCond(pt);
		pt.moveToParent();
		print(" loop\n");
		indentLevel++;
		pt.moveToChild(1);
		printSS(pt);
		pt.moveToParent();
		indentLevel--;
		indent();
		print("end;\n");
	}

	static void printIf(ParseTree pt) {
		assert pt.getNodeType() == NodeType.IF : "Node must be an IF";
		print("if ");
		pt.moveToChild(0);
		printCond(pt);
		pt.moveToParent();
		print(" then\n");
		indentLevel++;
		pt.moveToChild(1);
		printSS(pt);
		pt.moveToParent();
		indentLevel--;

		if (pt.getAlt() == 2) {
			indent();
			print("else\n");
			indentLevel++;
			pt.moveToChild(2);
			printSS(pt);
			pt.moveToParent();
			indentLevel--;
		}
		indent();
		print("end;\n");
	}

	static void printStmt(ParseTree pt) {
		assert pt.getNodeType() == NodeType.STMT : "Node must be a STMT";
		indent();
		int alt = pt.getAlt();
		pt.moveToChild(0);
		if (alt == 1) {
			printAssign(pt);
		} else if (alt == 2) {
			printIf(pt);
		} else if (alt == 3) {
			printLoop(pt);
		} else if (alt == 4) {
			printIn(pt);
		} else if (alt == 5) {
			printOut(pt);
		}
		pt.moveToParent();
	}

	static void printSS(ParseTree pt) {
		assert pt.getNodeType() == NodeType.SS : "Node must be an SS";
		pt.moveToChild(0);
		printStmt(pt);
		pt.moveToParent();

		if (pt.getAlt() == 2) {
			pt.moveToChild(1);
			printSS(pt);
			pt.moveToParent();
		}
	}

	static void printDecl(ParseTree pt) {
		indent();
		print("int ");
		pt.moveToChild(0);
		printIdList(pt);
		pt.moveToParent();
		print(";\n");
	}

	static void printDS(ParseTree pt) {
		assert pt.getNodeType() == NodeType.DS : "Node must be an DS";
		pt.moveToChild(0);
		printDecl(pt);
		pt.moveToParent();
		if (pt.getAlt() == 2) {
			pt.moveToChild(1);
			printDS(pt);
			pt.moveToParent();
		}
	}

	static void printProg(ParseTree pt) {
		assert pt.getNodeType() == NodeType.PROG : "Node must be an PROG";
		print("program\n");
		indentLevel++;
		pt.moveToChild(0);
		printDS(pt);
		pt.moveToParent();
		indentLevel--;
		print("begin\n");
		indentLevel++;
		pt.moveToChild(1);
		printSS(pt);
		pt.moveToParent();
		indentLevel--;
		print("end\n");
	}
}
