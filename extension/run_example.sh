#!/bin/sh
# Starts an example simulation utilizing the aspect-oriented next reaction method.

classpath="target/extension-0.0.2-SNAPSHOT.jar:target/dependency/*"

mvn clean package dependency:copy-dependencies
java -cp "$classpath" org.justinnk.ssamason.demo.ssamason.sir.SirModelWithUI
