package edu.osu.cse3341;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Helper {
	static Logger log;

	/**
	 * Helper class should not have a public constructor.
	 */
	private Helper() {
	}

	static void init() {
		log = Logger.getLogger("Main");

//        Handler handler = new ConsoleHandler();
//        handler.setLevel(Level.ALL);
//        log.addHandler(handler);
		log.setLevel(Level.SEVERE);
		log.fine("Logger created");
	}
}
