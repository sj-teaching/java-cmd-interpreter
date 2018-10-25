package edu.osu.cse3341;

import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
		Helper.init();

		Helper.log.info("Arguments: " + Arrays.toString(args));
		if (args.length != 1) {
			String msg = "Invalid number of arguments\nUsage: java Main <input-file-name>";
			Helper.log.severe(msg);
			System.exit(10);
		}

//		Tokenizer t = new TokenizerImpl();
//		t.tokenize(args[0]);
//		Helper.log.info("Tokenized file " + args[0]);
//		Helper.log.info("TokenStream: " + t.getTokenStream());
//
//		while (t.hasNext()) {
//			String token = t.currentToken();
//			System.out.println(t.code(token));
//			t.nextToken();
//		}

		try {
			ParseTree pt = Parser.parse(args[0]);
			Parser.print(pt);
		} catch (InterpreterException e) {
			Helper.log.info(e.getMessage());
			System.err.println(e.getMessage());
			System.exit(2);
		}

		Helper.log.info("Done!");
	}
}
