# JFLAGS = -g
JC = javac

PKG = edu/osu/cse3341

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES := $(shell ls $(PKG)/*.java)

default: classes

classes: $(CLASSES:.java=.class)

jar: classes MANIFEST.MF
jar cmvf MANIFEST.MF Core.jar $(PKG)/*.class

clean:
	$(RM) $(PKG)/*.class *.jar
