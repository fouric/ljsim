/* ljSim: this is the top level test program for Links and Joints *//* adapted from the program of 25 April 1997 *//* * This program represents Links and Joints and simulates them. * There is an event queue in the class Chore. * Each executable item has a DO routine that gets called when it's Chore gets done. * The Commands class provides this simulation service. **//* updated 2 June 2017 *///import ljSim.basicA.Messenger;import ljSim.commands.Command;import ljSim.parts.Factory;import ljSim.components.Joint;import ljSim.components.Link;public class ljSimulate {//making messenger//    private static Messenger MSG = Messenger.please("Test code", 2);//    private static Factory myFactory = new Factory(MSG);	public static void main(String argv[]) {		System.out.println("STARTING TEST");		// Comp.testMe();		// Chore.testMe();		// Factory.testFifo();		// Factory.testQ();		// myFactory.testTwinFifoS();		Factory.testRoundRobinA();		// Factory.testRoundRobinB();		// Factory.testDemandMerge();		// Messenger.testMe();		// When.testMe();		// TestComponent.testMe();		// JournalCommand.testMe();		// AtestofP3.pathTest();		// pathTest();		System.out.println("END OF TEST");	}// end of main///About to do	private static void runIt() {		Link.clearAllLinks();		Joint.clearAllJoints();		Command.printPendingTasks();		Command.doManyTasks(300);		return;	}// end of runIt	private static void wrapIt() {		Command.printPendingTasks();		Link.printStatistics();		Joint.printStatistics();	}// end of wrapIt}// end of class ljSimulate/* * class LJtester { *  * public static void LinkingTest() { Component TOP = Component.getTheParent(); * Link L02 = Link.please("Link02", TOP); Link L12 = Link.please("Link12", TOP); * Link L23 = Link.please("Link23", TOP); Link L34 = Link.please("Link34", TOP); * Link L35 = Link.please("Link35", TOP); // Link P24 = Link.please("Link24", * TOP); Joint P0 = Joint.getSourceJoint("Joint0", TOP); Joint P1 = * Joint.getSourceJoint("Joint1", TOP); Joint P2 = Joint.getEmptyJoint("Joint2", * TOP); Joint P3 = Joint.getEmptyJoint("Joint3", TOP); Joint P4 = * Joint.getSinkJoint("Joint4", TOP); Joint P5 = Joint.getSinkJoint("Joint5", * TOP); // Port PT = new Port("PortT"); P12.attach(P1); P2.attach(P12); * P02.attach(P0); P2.attach(P02); P23.attach(P2); P3.attach(P23); * P34.attach(P3); P4.attach(P34); P35.attach(P3); // second attachment * P5.attach(P35); *  * // P12.attach(P1); //should make an error // P2.attach(P12); // should make * an error *  * Link.printTopology(); Joint.printTopology(); // Port.printTopology(); *  * Link.clearAllLinks(Delay.of(10), Value.zeroValue); * Joint.clearAllJoints(Delay.of(20), Value.zeroValue); *  * Chore.printChoreList(); Chore.doManyChores(6); Chore.printChoreList(); * Chore.doManyChores(300); Chore.printChoreList(); Chore.printStatistics(); * Link.printStatistics(); Joint.printStatistics(); *  * return; }// end of LinkingTest *  * }// end of Class AtestofP3 */