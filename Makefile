ifeq ($(origin NETLOGO), undefined)
  NETLOGO=../..
endif

ifeq ($(origin SCALA_HOME), undefined)
  SCALA_HOME=../..
endif

ifneq (,$(findstring CYGWIN,$(shell uname -s)))
  COLON=\;
  SCALA_HOME := `cygpath -up "$(SCALA_HOME)"`
else
  COLON=:
endif

SRCS=$(wildcard src/*.scala)

sample-scala.jar: $(SRCS) manifest.txt Makefile
	mkdir -p classes
	$(SCALA_HOME)/bin/scalac -deprecation -unchecked -encoding us-ascii -classpath $(NETLOGO)/NetLogo.jar -d classes $(SRCS)
	jar cmf manifest.txt sample-scala.jar -C classes .

sample-scala.zip: sample-scala.jar
	rm -rf sample-scala
	mkdir sample-scala
	cp -rp sample-scala.jar README.md Makefile src manifest.txt tests.txt sample-scala
	zip -rv sample-scala.zip sample-scala
	rm -rf sample-scala
