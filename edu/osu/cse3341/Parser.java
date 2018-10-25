package edu.osu.cse3341;

import java.util.HashMap;
import java.util.Map;

import edu.osu.cse3341.ParseTree.NodeType;

public class Parser {
	static Tokenizer t;
	static Map<String, Integer> st;

	private Parser() {
		/* You can't create an instance of a parser - yet */
	}

	static void matchConsume(String exptok) {
		String actual = t.currentToken();
		if ((exptok.equals("INT") && t.code(actual) != 31) // int code
				|| (exptok.equals("ID") && t.code(actual) != 32) // id code
				|| (exptok.equals("~EOF~") && t.code(actual) != 33) // eof code
				|| (!exptok.equals("INT") && !exptok.equals("ID") && !exptok.equals("~EOF~")
						&& !exptok.equals(actual))) {
			Helper.log.severe("Syntax Error! Expected: " + exptok + ", found: " + actual);
			System.exit(2);
		}
		t.nextToken();
		Helper.log.info("Consumed: " + actual);
	}

	static void parseInt(ParseTree pt) {
		pt.setAlt(1);
		pt.setNodeType(NodeType.INT);
		matchConsume("INT");
	}

	static void parseFac(ParseTree pt) {
		pt.setNodeType(NodeType.FAC);
		if (t.code(t.currentToken()) == 31) {// INT
			pt.setAlt(1);
			pt.addChild();
			pt.moveToChild(0);
			parseInt(pt);
			pt.moveToParent();
		} else if (t.code(t.currentToken()) == 32) { // ID
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(0);
			parseId(pt);
			pt.moveToParent();
		} else if (t.currentToken().equals("(")) {
			matchConsume("(");
			pt.setAlt(3);
			pt.addChild();
			pt.moveToChild(0);
			parseExp(pt);
			pt.moveToParent();
			matchConsume(")");
		}
	}

	static void parseTerm(ParseTree pt) {
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

	static void parseExp(ParseTree pt) {
		pt.setNodeType(NodeType.EXP);
		pt.addChild();
		pt.moveToChild(0);
		parseTerm(pt);
		pt.moveToParent();

		if (t.currentToken().equals("+")) {
			matchConsume("+");
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(1);
			parseExp(pt);
			pt.moveToParent();
		} else if (t.currentToken().equals("-")) {
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

	static void parseId(ParseTree pt) {
		pt.setNodeType(NodeType.ID);
		String id = t.currentToken();
		t.nextToken();
		Helper.log.info("Consumed: " + id);
		if (!st.containsKey(id)) {
			st.put(id, 0);
		}
		pt.setIdString(id);
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

	static void parseComp(ParseTree pt) {
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

	static void parseCond(ParseTree pt) {
		pt.setNodeType(NodeType.COND);

		String tok = t.currentToken();
		if (tok.equals("!")) {
			pt.setAlt(2);
			pt.addChild();
			pt.moveToChild(0);
			parseCond(pt);
			pt.moveToParent();
		} else if (tok.equals("[")) {
			t.nextToken();
			Helper.log.info("Consumed " + tok);

			pt.addChild();
			pt.moveToChild(0);
			parseCond(pt);
			pt.moveToParent();

			tok = t.currentToken();
			if (tok.equals("and")) {
				pt.setAlt(3);
			} else if (tok.equals("or")) {
				pt.setAlt(4);
			}
			t.nextToken();
			Helper.log.info("Consumed " + tok);

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

	static void parseIdList(ParseTree pt) {
		pt.setNodeType(NodeType.IDLIST);
		pt.addChild();
		pt.moveToChild(0);
		parseId(pt);
		pt.moveToParent();

		String tok = t.currentToken();
		if (tok.equals(",")) {
			t.nextToken();
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

	static void parseIn(ParseTree pt) {
		pt.setNodeType(NodeType.IN);
		matchConsume("read");
		pt.addChild();
		pt.moveToChild(0);
		parseIdList(pt);
		pt.moveToParent();

		matchConsume(";");
	}

	static void parseOut(ParseTree pt) {
		pt.setNodeType(NodeType.OUT);
		matchConsume("write");
		pt.addChild();
		pt.moveToChild(0);
		parseIdList(pt);
		pt.moveToParent();

		matchConsume(";");
	}

	static void parseAssign(ParseTree pt) {
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

	static void parseStmt(ParseTree pt) {
		pt.setNodeType(NodeType.STMT);
		String tok = t.currentToken();

		pt.addChild();
		pt.moveToChild(0);

		if (t.code(tok) == 32) {
			parseAssign(pt);
		} else if (tok.equals("if")) {
			parseIf(pt);
		} else if (tok.equals("while")) {
			parseLoop(pt);
		} else if (tok.equals("read")) {
			parseIn(pt);
		} else if (tok.equals("write")) {
			parseOut(pt);
		}

		pt.moveToParent();
	}

	static void parseSS(ParseTree pt) {
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

	static void parseLoop(ParseTree pt) {
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

	static void parseIf(ParseTree pt) {
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

	static void parseDecl(ParseTree pt) {
		pt.setNodeType(NodeType.DECL);
		matchConsume("int");
		pt.addChild();
		pt.moveToChild(0);
		parseIdList(pt);
		pt.moveToParent();

		matchConsume(";");
	}

	static void parseDS(ParseTree pt) {
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

	static void parseProg(ParseTree pt) {
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

	public static void main(String[] args) {
		Helper.init();

		t = new TokenizerImpl();
		t.tokenize("data/parserTest1.core");
		Helper.log.info("Tokens: " + t.getTokenStream());

		st = new HashMap<>();

		ParseTree pt = new ParseTreeImpl();
		parseProg(pt);
		matchConsume("~EOF~");
		System.out.println("Done");
	}
}
