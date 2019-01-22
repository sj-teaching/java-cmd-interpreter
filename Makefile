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
	jar cmvf MANIFEST1.MF Tokenizer.jar $(PKG)/Token.class $(PKG)/Tokenizer.class	\
																			$(PKG)/InvalidTokenException.class				\
																			$(PKG)/InterpreterException.class
clean:
	$(RM) $(PKG)/*.class *.jar
