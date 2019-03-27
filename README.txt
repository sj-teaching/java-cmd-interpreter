CORE Interpreter
================

This is a public folder for the CORE interpreter implemented in Java. It
provides an executable jar and some test cases. You can use it to get a feel
of the expected behavior of the interpreter and also check the expected output
for the given test cases.

Usage:

$ java -jar Core.jar <option> <test file>

where
	<option> includes
		-t	Run tokenizer only
		-p	Run parser only
		-i	Run the full interpreter

Example:

$ java -jar Core.jar -i data/test-pa3/validAllOneLine.core

=================

Author: Swaroop Joshi
Email: joshi.127@osu.edu
Date: 2019-03-27
