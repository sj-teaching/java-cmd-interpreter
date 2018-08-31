JFLAGS = -g
JC = javac

PKG = edu/osu/cse3341

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = $(PKG)/Tokenizer.java $(PKG)/TokenizerImpl.java $(PKG)/Main.java

default: classes

classes: $(CLASSES:.java=.class)

jar: $(PKG)/*.class MANIFEST.MF
	jar cmvf MANIFEST.MF Tokenizer.jar $(PKG)/*.class

run: *.jar
	java -jar Tokenizer.jar

clean:
	$(RM) *.class *.jar
