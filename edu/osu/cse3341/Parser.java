package edu.osu.cse3341;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.osu.cse3341.ParseTree.NodeType;

public class Parser {
    static Tokenizer t;

    private static final int OFFSET = 2;
    private static int indentLevel = 0;

    private static Logger log;

    private Parser() {
        /* cannot be instantiated - use all static methods */
    }

    public static ParseTree parse(String filepath) throws CoreError.InterpreterException {
        t = new Tokenizer();
        t.tokenize(filepath);

        ParseTree pt = new ParseTreeImpl();
        parseProg(pt);
        matchConsume(Token.EOF);
        return pt;
    }

    public static void print(ParseTree pt) {
        printProg(pt);
    }

    // --- Helper Methods ---
    static void matchConsume(int exptok) throws CoreError.InterpreterException {
        Token actual = t.currentToken();
        if (exptok == actual.code) {
            t.nextToken();
            Main.logInfo("Consumed: " + actual.name);
        } else {
            String msg = "Syntax Error: [Line " + actual.line + "] Expecetd: " + exptok + ", found: " + actual.name;
            Main.logInfo(msg);
            throw new CoreError.UnexpectedTokenException(msg);
        }
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

    static void parseInt(ParseTree pt) throws CoreError.InterpreterException {
        pt.setAlt(1);
        pt.setNodeType(NodeType.INT);
        int val = Integer.parseInt(t.currentToken().name);
        pt.setValue(val);
        matchConsume(Token.NUM);
    }

    static void parseId(ParseTree pt, boolean declare, boolean init) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.ID);
        String id = t.currentToken().name;

        matchConsume(Token.ID);

        if (declare && pt.isDeclared(id)) {
            throw new CoreError.IdRedeclException("[Line " + t.currentToken().line + "] " + id + " already declared");
        }
        if (!declare && !pt.isDeclared(id)) {
            throw new CoreError.IdUndeclException(
                                                  "[Line " + t.currentToken().line + "] Using undeclared variable " + id);
        }

        pt.setIdString(id);

        Main.logInfo("parseId - id = " + id + ", init = " + init);
        if (!declare) {
            if (!init && !pt.isInitialized(id)) {
                throw new CoreError.UninitializedException(
                                                           "[Line " + t.currentToken().line + "] Use of variable " + id + " before initialization");
            }
            if (init) {
                pt.initialize(id);
            }
        }
    }

    static void parseFac(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.FAC);

        Token tok = t.currentToken();
        if (tok.code == Token.NUM) {
            pt.setAlt(1);
            pt.addChild();
            pt.moveToChild(0);
            parseInt(pt);
            pt.moveToParent();
        } else if (tok.code == Token.ID) { // ID
            pt.setAlt(2);
            pt.addChild();
            pt.moveToChild(0);
            parseId(pt, false, false);
            pt.moveToParent();
        } else if (tok.code == Token.LPAREN) {
            matchConsume(Token.LPAREN);
            pt.setAlt(3);
            pt.addChild();
            pt.moveToChild(0);
            parseExp(pt);
            pt.moveToParent();
            matchConsume(Token.RPAREN);
        } else {
            throw new CoreError.UnexpectedTokenException("Syntax Error: [Line " + t.currentToken().line
                                                         + "] Expected <num>, <id>, or <lparen>, found " + tok.name);
        }
    }

    static void parseTerm(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.TERM);
        pt.addChild();
        pt.moveToChild(0);
        parseFac(pt);
        pt.moveToParent();

        if (t.currentToken().code == Token.STAR) {
            matchConsume(Token.STAR);
            pt.setAlt(2);
            pt.addChild();
            pt.moveToChild(1);
            parseTerm(pt);
            pt.moveToParent();
        } else {
            pt.setAlt(1);
        }
    }

    static void parseExp(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.EXP);
        pt.addChild();
        pt.moveToChild(0);
        parseTerm(pt);
        pt.moveToParent();

        Token tok = t.currentToken();
        if (tok.code == Token.PLUS) {
            matchConsume(Token.PLUS);
            pt.setAlt(2);
            pt.addChild();
            pt.moveToChild(1);
            parseExp(pt);
            pt.moveToParent();
        } else if (tok.code == Token.MINUS) {
            matchConsume(Token.MINUS);
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

        Token tok = t.currentToken();
        if (tok.code == Token.NEQ) {
            pt.setAlt(1);
        } else if (tok.code == Token.EQ) {
            pt.setAlt(2);
        } else if (tok.code == Token.LT) {
            pt.setAlt(3);
        } else if (tok.code == Token.GT) {
            pt.setAlt(4);
        } else if (tok.code == Token.LEQ) {
            pt.setAlt(5);
        } else if (tok.code == Token.GEQ) {
            pt.setAlt(6);
        }
        t.nextToken();
        Main.logInfo("Consumed " + tok);
    }

    static void parseComp(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.COMP);
        matchConsume(Token.LPAREN);
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

        matchConsume(Token.RPAREN);
    }

    static void parseCond(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.COND);

        Token tok = t.currentToken();
        if (tok.code == Token.NOT) {
            matchConsume(Token.NOT);
            pt.setAlt(2);
            pt.addChild();
            pt.moveToChild(0);
            parseCond(pt);
            pt.moveToParent();
        } else if (tok.code == Token.LBRACK) {
            matchConsume(Token.LBRACK);

            pt.addChild();
            pt.moveToChild(0);
            parseCond(pt);
            pt.moveToParent();

            tok = t.currentToken();
            if (tok.code == Token.AND) {
                pt.setAlt(3);
                matchConsume(Token.AND);
            } else if (tok.code == Token.OR) {
                pt.setAlt(4);
                matchConsume(Token.OR);
            }

            pt.addChild();
            pt.moveToChild(1);
            parseCond(pt);
            pt.moveToParent();

            matchConsume(Token.RBRACK);
        } else {
            pt.setAlt(1);
            pt.addChild();
            pt.moveToChild(0);
            parseComp(pt);
            pt.moveToParent();
        }
    }

    static void parseIdList(ParseTree pt, boolean declare, boolean init) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.IDLIST);
        pt.addChild();
        pt.moveToChild(0);
        parseId(pt, declare, init);
        pt.moveToParent();

        Token tok = t.currentToken();
        if (tok.code == Token.COMMA) {
            matchConsume(Token.COMMA);
            Main.logInfo("Consumed " + tok.name);

            pt.setAlt(2);
            pt.addChild();
            pt.moveToChild(1);
            parseIdList(pt, declare, init);
            pt.moveToParent();
        } else {
            pt.setAlt(1);
        }
    }

    static void parseIn(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.IN);
        matchConsume(Token.READ);
        pt.addChild();
        pt.moveToChild(0);
        parseIdList(pt, false, true);
        pt.moveToParent();

        matchConsume(Token.SEMICOL);
    }

    static void parseOut(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.OUT);
        matchConsume(Token.WRITE);
        pt.addChild();
        pt.moveToChild(0);
        parseIdList(pt, false, false);
        pt.moveToParent();

        matchConsume(Token.SEMICOL);
    }

    static void parseAssign(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.ASSIGN);

        pt.addChild();
        pt.moveToChild(0);
        parseId(pt, false, true);
        pt.moveToParent();

        matchConsume(Token.ASSIGN);

        pt.addChild();
        pt.moveToChild(1);
        parseExp(pt);
        pt.moveToParent();

        matchConsume(Token.SEMICOL);
    }

    static void parseLoop(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.LOOP);
        matchConsume(Token.WHILE);
        pt.addChild();
        pt.moveToChild(0);
        parseCond(pt);
        pt.moveToParent();

        matchConsume(Token.LOOP);
        pt.addChild();
        pt.moveToChild(1);
        parseSS(pt);
        pt.moveToParent();

        matchConsume(Token.END);
        matchConsume(Token.SEMICOL);
    }

    static void parseIf(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.IF);
        pt.setAlt(1);

        matchConsume(Token.IF);
        pt.addChild();
        pt.moveToChild(0);
        parseCond(pt);
        pt.moveToParent();

        matchConsume(Token.THEN);
        pt.addChild();
        pt.moveToChild(1);
        parseSS(pt);
        pt.moveToParent();

        if (t.currentToken().code == Token.ELSE) {
            matchConsume(Token.ELSE);
            pt.addChild();
            pt.moveToChild(2);
            parseSS(pt);
            pt.moveToParent();

            pt.setAlt(2);
        }

        matchConsume(Token.END);
        matchConsume(Token.SEMICOL);
    }

    static void parseStmt(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.STMT);
        Token tok = t.currentToken();

        pt.addChild();
        pt.moveToChild(0);

        if (tok.code == Token.ID) {
            parseAssign(pt);
            pt.moveToParent();
            pt.setAlt(1);
        } else if (tok.code == Token.IF) {
            parseIf(pt);
            pt.moveToParent();
            pt.setAlt(2);
        } else if (tok.code == Token.WHILE) {
            parseLoop(pt);
            pt.moveToParent();
            pt.setAlt(3);
        } else if (tok.code == Token.READ) {
            parseIn(pt);
            pt.moveToParent();
            pt.setAlt(4);
        } else if (tok.code == Token.WRITE) {
            parseOut(pt);
            pt.moveToParent();
            pt.setAlt(5);
        } else {
            throw new CoreError.UnexpectedTokenException("Syntax Error: [Line " + t.currentToken().line
                                                         + "] Expected id, if, while, read, or write, found " + tok.name);
        }
    }

    static void parseSS(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.SS);

        pt.addChild();
        pt.moveToChild(0);
        parseStmt(pt);
        pt.moveToParent();

        Token tok = t.currentToken();
        // Observation from the grammar: Stmt-Seq's are followed by "end" or "else" only
        if (tok.code == Token.END || tok.code == Token.ELSE) {
            pt.setAlt(1);
        } else {
            pt.setAlt(2);
            pt.addChild();
            pt.moveToChild(1);
            parseSS(pt);
            pt.moveToParent();
        }
    }

    static void parseDecl(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.DECL);
        matchConsume(Token.INT);
        pt.addChild();
        pt.moveToChild(0);
        parseIdList(pt, true, false);
        pt.moveToParent();

        matchConsume(Token.SEMICOL);
    }

    static void parseDS(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.DS);

        pt.addChild();
        pt.moveToChild(0);
        parseDecl(pt);
        pt.moveToParent();

        if (t.currentToken().code == Token.INT) {
            pt.setAlt(2);
            pt.addChild();
            pt.moveToChild(1);
            parseDS(pt);
            pt.moveToParent();
        }
    }

    static void parseProg(ParseTree pt) throws CoreError.InterpreterException {
        pt.setNodeType(NodeType.PROG);
        matchConsume(Token.PROGRAM);
        pt.addChild();
        pt.moveToChild(0);
        parseDS(pt);
        pt.moveToParent();

        matchConsume(Token.BEGIN);
        pt.addChild();
        pt.moveToChild(1);
        parseSS(pt);
        pt.moveToParent();

        matchConsume(Token.END);
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
        print("program \n");
        indentLevel++;
        pt.moveToChild(0);
        printDS(pt);
        pt.moveToParent();
        //		indentLevel--;
        indent();
        print("begin\n");
        indentLevel++;
        pt.moveToChild(1);
        printSS(pt);
        pt.moveToParent();
        indentLevel--;
        indent();
        print("end\n");
    }
}
