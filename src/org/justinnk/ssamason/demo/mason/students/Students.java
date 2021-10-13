package org.justinnk.ssamason.demo.mason.students;

import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.Double2D;

public class Students extends SimState {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Continuous2D yard = new Continuous2D(1.0, 100, 100);
	public Network buddies = new Network(false);
	public int numStudents = 50;
	public double forceToSchoolMultiplier = 0.01;
	public double randomMultiplier = 0.1;

	public Students(long seed) {
		super(seed);
	}

	public void start() {
		super.start();

		// clear the yard
		yard.clear();

		// clear the buddies
		buddies.clear();

		// add some students to the yard
		for (int i = 0; i < numStudents; i++) {
			Student student = new Student();
			yard.setObjectLocation(student, new Double2D(yard.getWidth() * 0.5 + random.nextDouble() - 0.5,
					yard.getHeight() * 0.5 + random.nextDouble() - 0.5));
			buddies.addNode(student);
			schedule.scheduleRepeating(student);
		}

		// define like/dislike relationships
		Bag students = buddies.getAllNodes();
		for (int i = 0; i < students.size(); i++) {
			Object student = students.get(i);

			// who does he/she like?
			Object studentB = null;
			do {
				studentB = students.get(random.nextInt(students.numObjs));
			} while (student == studentB);
			double buddiness = random.nextDouble();
			buddies.addEdge(student, studentB, buddiness);

			// who does he/she dislike?
			do {
				studentB = students.get(random.nextInt(students.numObjs));
			} while (student == studentB);
			buddiness = random.nextDouble();
			buddies.addEdge(student, studentB, -buddiness);
		}
	}

	public static void main(String[] args) {
		doLoop(Students.class, args);
		System.exit(0);
	}

}
