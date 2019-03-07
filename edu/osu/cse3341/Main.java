package edu.osu.cse3341;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    /**
     * Handler for the logger.
     */
    static Logger log;

    public static void logInfo(String msg) {
        log.info(msg);
    }

    public static void raiseTokError(String msg) throws CoreError.InvalidTokenException {
        String excMsg = "Invalid Token: " + msg;
        log.info(excMsg);
        throw new CoreError.InvalidTokenException(excMsg);
    }

    public static void main(String[] args) {
        log = Logger.getLogger("Main");
        log.setLevel(Level.SEVERE);
        log.info("Arguments: " + Arrays.toString(args));

        String msg = "Invalid number of arguments\n"
            + "Usage: java -jar core.jar <option> <test-file>\n"
            + "where\n"
            + "\t<option> includes\n"
            + "\t\t-t\tRun tokenizer only\n"
            + "\t\t-p\tRun parser only\n"
            + "\t\t-i\tRun the full interpreter\n\n";

        try {
            if (args.length != 2) {
                throw new CoreError.InvalidCmdLineArgsException(msg);
            }

            String option = args[0];
            String testFile = args[1];

            if (option.equals("-t")) {
                Tokenizer t = new Tokenizer();
                t.tokenize(testFile);
                while (t.hasNext()) {
                    Token token = t.currentToken();
                    System.out.println(token);
                    t.nextToken();
                }
            } else if (option.equals("-p")) {
                ParseTree pt = Parser.parse(testFile);
                Parser.print(pt);
            } else if (option.equals("-i")) {
                ParseTree pt = Parser.parse(args[0]);
                Interpreter.execProg(pt);
            } else {
                throw new CoreError.InvalidCmdLineArgsException(msg);
            }
        } catch(CoreError.InterpreterException e){
            log.severe(e.getMessage());
            System.exit(2);
        }
    }
}
