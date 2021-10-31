#!/bin/sh
# Starts an example simulation utilizing the aspect-oriented next reaction method.

#java -cp "target/demo-0.0.1-SNAPSHOT-jar-with-dependencies.jar" org.justinnk.ssamason.demo.ssamason.sir.SirModelWithUI
classpath="demo/target/demo-0.0.1-SNAPSHOT.jar:demo/target/dependency/*"

cd ..
mvn -U -pl demo -am clean package dependency:copy-dependencies
java -cp "$classpath" org.justinnk.ssamason.demo.ssamason.sir.SirModelWithUI
