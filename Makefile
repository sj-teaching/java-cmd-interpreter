# JFLAGS = -g
JC = javac

PKG = edu/osu/cse3341

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES := $(shell ls $(PKG)/*.java)

default: classes

classes: $(CLASSES:.java=.class)

tokenizer: classes MANIFEST1.MF
	jar cmvf MANIFEST1.MF Tokenizer.jar $(PKG)/Token*.class $(PKG)/CoreError*.class

parser: classes MANIFEST2.MF
	jar cmvf MANIFEST2.MF Parser.jar $(PKG)/Token*.class $(PKG)/CoreError*.class \
																			$(PKG)/Parser*.class										 \
																			$(PKG)/ParseTree*.class									 \
																			$(PKG)/SymbolTable*.class

interpreter: classes MANIFEST3.MF
	jar cmvf MANIFEST3.MF Interpreter.jar $(PKG)/*.class

clean:
	$(RM) $(PKG)/*.class *.jar
