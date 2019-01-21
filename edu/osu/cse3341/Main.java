package edu.osu.cse3341;

import java.util.Arrays;

public class Main {
	public static void main(String[] args) throws InvalidTokenException {
		Helper.init();

		Helper.log.info("Arguments: " + Arrays.toString(args));
		if (args.length != 1) {
			String msg = "Invalid number of arguments\nUsage: java -jar <jar file> <test file>";
			Helper.log.severe(msg);
			System.exit(10);
		}

		Tokenizer t = new TokenizerImpl(args[0]);
		Helper.log.info("Tokening file " + args[0]);

		while (t.currentToken().type() != Token.Type.EOF) {
			Token token = t.currentToken();
			System.out.println(token);
			t.nextToken();
		}

//		try {
//			ParseTree pt = Parser.parse(args[0]);
//			Parser.print(pt);
//			System.out.println("\n\n*** Result: ***\n");
//			Interpreter.execProg(pt);
//		} catch (InterpreterException e) {
//			Helper.log.info(e.getMessage());
//			System.err.println(e.getMessage());
//			System.exit(2);
//		}

		Helper.log.info("Done!");
	}
}
