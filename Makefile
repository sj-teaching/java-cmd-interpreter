# JFLAGS = -g
JC = javac

PKG = edu/osu/cse3341

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = $(PKG)/Helper.java $(PKG)/ParseTreeImpl.java $(PKG)/IdRedeclException.java \
	$(PKG)/Parser.java $(PKG)/IdUndeclException.java $(PKG)/Token.java \
	$(PKG)/InterpreterException.java $(PKG)/Tokenizer.java \
	$(PKG)/InvalidTokenException.java    $(PKG)/TokenizerImpl.java \
	$(PKG)/Main.java                     $(PKG)/UnexpectedTokenException.java \
	$(PKG)/ParseTree.java


default: classes

classes: $(CLASSES:.java=.class)

jar: classes MANIFEST.MF
	jar cmvf MANIFEST.MF Parser.jar $(PKG)/*.class

run: *.jar
	java -jar Parser.jar

clean:
	$(RM) $(PKG)/*.class *.jar
