/* ljSim: this is the top level test program for Links and Joints */
/* adapted from the program of 25 April 1997 */
/*
 * This program represents Links and Joints and simulates them.
 * There is an event queue in the class Chore.
 * Each executable item has a DO routine that gets called when it's Chore gets done.
 * The Commands class provides this simulation service.
 **/
/* updated 2 June 2017 */

import ljSim.parts.Factory;

public class ljSimulate {

	public static void main(String argv[]) {
		System.out.println("STARTING TEST");
		Factory.testRoundRobinA(); // Factory is a class, but we can call testRoundRobinA because it's a static method
		System.out.println("END OF TEST");
	}

	/*
	private static void runIt() {
		Link.clearAllLinks();
		Joint.clearAllJoints();
		Command.printPendingTasks();
		Command.doManyTasks(300);
		return;
	}

	private static void wrapIt() {
		Command.printPendingTasks();
		Link.printStatistics();
		Joint.printStatistics();
	}
	*/

}
