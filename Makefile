ifeq ($(origin NETLOGO), undefined)
  NETLOGO=../..
endif

ifeq ($(origin SCALA_HOME), undefined)
  SCALA_HOME=../..
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
