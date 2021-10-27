#!/bin/sh
# Runs the evaluation used in the thesis/paper (minus the profiles).

classpath="target/ssamason-0.0.2-SNAPSHOT.jar:target/dependency/*"

mvn clean package dependency:copy-dependencies
java -cp "$classpath" org.justinnk.ssamason.evaluation.Evaluator nrm
java -cp "$classpath" org.justinnk.ssamason.evaluation.Evaluator odm
java -cp "$classpath" org.justinnk.ssamason.evaluation.Evaluator vanilla
java -cp "$classpath" org.justinnk.ssamason.evaluation.Evaluator frm
java -cp "$classpath" org.justinnk.ssamason.evaluation.Evaluator ldm
java -cp "$classpath" org.justinnk.ssamason.evaluation.Evaluator frm_overhead
python -m venv .venv
. .venv/bin/activate # . is the POSIX version of source
pip install -r requirements.txt
python plot_correctness.py
python plot_performance.py
deactivate
