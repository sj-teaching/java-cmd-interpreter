JFLAGS = -g
JC = javac

PKG = edu/osu/cse3341

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = $(PKG)/Tokenizer.java $(PKG)/TokenizerImpl.java $(PKG)/ParseTree.java $(PKG)/ParseTreeImpl.java $(PKG)/Parser.java

default: classes

classes: $(CLASSES:.java=.class)

jar: $(PKG)/*.class MANIFEST.MF
	jar cmvf MANIFEST.MF Parser.jar $(PKG)/*.class

run: *.jar
	java -jar Parser.jar

clean:
	$(RM) *.class *.jar
