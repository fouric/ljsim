/* Factory.java has a collection of methods to build complex FIFOs */
/* IES  17 May 1997 */
/* updated 9 September 1997 */
/* updated IES 6 June 1997 */

package ljSim.parts;

import ljSim.basicA.Time;
import ljSim.commands.Command;
import ljSim.commands.JointCommand;
import ljSim.commands.SortedJointCommand;
import ljSim.commands.SquareQueue;
import ljSim.jointPkg.DemandMergeJoint;
import ljSim.jointPkg.JointFactory;
import ljSim.jointPkg.OneInOneOutJoint;
import ljSim.jointPkg.RoundRobinForkJoint;
import ljSim.jointPkg.RoundRobinJoinJoint;
import ljSim.jointPkg.RoundRobinJoint;
import ljSim.jointPkg.SinkJoint;
import ljSim.jointPkg.SourceJoint;
import ljSim.components.Component;
import ljSim.components.Joint;
import ljSim.components.Link;

public class Factory {

	public Factory() {
		return;
	}

	// to test SquareQueue
	static public void testQ() {
		Command.sq = new SquareQueue();
		SortedJointCommand[] ca;
		ca = new SortedJointCommand[10];
		JointCommand[] cb;
		cb = new JointCommand[10];
		Joint[] ja;
		ja = new Joint[10];
		Joint j = new SinkJoint("testJoint", null);
		//Link la = new Link("testLink", j);
		for (int i = 1; i < 9; i++) {
			ca[i] = new SortedJointCommand(j);
			ca[i].setTime(new Time(i * 1000));
			ja[i] = new OneInOneOutJoint(null, j);
			cb[i] = ja[i].makeAwakeUpCommand();
			cb[i].setTime(new Time(i * 400));
		}
		ca[5].setTime(new Time(3500)); // this one is out of order

		Command.sq.printPendingTasks();

		for (int i = 1; i < 9; i++) {
			//Time tt = new Time(i * 1010);
			ca[i].enQueueMe();
		}

		Command.sq.printPendingTasks();

		for (int i = 1; i < 9; i++) {
			Time tt = new Time(i * 440);
			cb[i].enQueueMe(tt);
		}

		Command.sq.printPendingTasks();

	}

	/// has no usable
	// This method tests a single long FIFO

	static public void testFifo() {
		Command.reset();
		Component p = new Component(null, "ROOT", true);
		// make the fixed Joints
		SourceJoint src = new SourceJoint("src", p);
		SinkJoint snk = new SinkJoint("snk", p);

		// wire them up
		JointFactory.makeFIFO(p, src, snk, 8);

		// exhibit the topology
		p.printTheComponents();
		Joint.checkJointTopology();
		Joint.clearAllJoints();
		Link.clearAllLinks();
		Link.printTopology();
		Joint.printTopology();

		// make initializers and do
		src.makeStartTask(Time.zeroTime);
		snk.makeQuitTask(Time.theLastTime);
		Command.printPendingTasks();
		Command.doManyTasks(200);
		Link.printStatistics();
		Joint.printStatistics();
		Command.printStatistics();
	}
	/// out of sequence
	// this method connects a roundRobinFork and roundRobin Joint
	// connected by three short FIFIs.

	public void testTwinFifoS() {
		// this is a test of TWO FIFOs in parallel
		Command.reset();
		Component p = new Component(null, "ROOT", true);
		Component groupA = new Comp("groupA", p);
		Component groupB = new Comp("groupB", p);
		SourceJoint src = new SourceJoint("src", groupA);
		SinkJoint snk = new SinkJoint("snk", groupA);
		JointFactory.makeFIFO(groupA, src, snk, 5);
		JointFactory.makeFIFO(groupB, src, snk, 3);
		p.printTheComponents();
		Joint.checkJointTopology();
		Joint.clearAllJoints();
		Link.clearAllLinks();
		Link.printTopology();
		Joint.printTopology();
		// OneInOneOutJoint.printTopology();

		src.makeStartTask(Time.zeroTime);
		Command.doManyTasks(200);
		Link.printStatistics();
		Joint.printStatistics();
		Command.printStatistics();

		return;
	}

	static public void testRoundRobinA() {
		// what is this Command.setup()? Command is a class? yes, an abstract class.
		// reset initializes the member variable sq of type SquareQueue to be a new SquareQueue object
		Command.reset();
		Component root = new Component(null, "ROOT", true);
		System.out.println("current parent is " + root);
		// ...why not just `new SourceJoint("src", p);`?
		// note: these used to be "SourceJoint.please" instead of "new SourceJoint"
		SourceJoint src = new SourceJoint("src", root);
		SinkJoint snk = new SinkJoint("snk", root);

		RoundRobinForkJoint f = new RoundRobinForkJoint("forker", root);
		RoundRobinJoinJoint j = new RoundRobinJoinJoint("joiner", root);
		JointFactory.makeFIFO(root, src, f, 2);
		JointFactory.makeRR(root, f, j, 3, 2);
		JointFactory.makeFIFO(root, j, snk, 2);

		root.printTheComponents();
		Joint.checkJointTopology();
		Joint.clearAllJoints();
		// Link.clearAllLinks();
		Link.printTopology();
		Joint.printTopology();
		// why did we print the link topology twice?
		//Link.printTopology();
		/// has no usable
		src.makeStartTask(Time.zeroTime);
		snk.makeQuitTask(Time.theLastTime);
		Command.printPendingTasks();
		Command.doManyTasks(200);
		Link.printStatistics();
		Joint.printStatistics();
		Command.printStatistics();
	}

	static public void testRoundRobinB() {
		Command.reset();
		Component p = new Component(null, "ROOT", true);
		SourceJoint src = new SourceJoint("src", p);
		SinkJoint snk = new SinkJoint("snk", p);

		// make the Forker and joiner
		RoundRobinJoint f = new RoundRobinJoint("forker", p, "forkType");
		f.addActions(1, 3);
		RoundRobinJoint j = new RoundRobinJoint("joiner", p, "joinType");
		j.addActions(3, 1);
		JointFactory.makeFIFO(p, src, f, 2);
		JointFactory.makeRR(p, f, j, 3, 2);
		JointFactory.makeFIFO(p, j, snk, 2);

		p.printTheComponents();
		Joint.printTopology();
		Joint.checkJointTopology();
		Joint.clearAllJoints();
		Link.printStatistics();
		// Link.clearAllLinks();
		Link.printTopology();

		src.makeStartTask(Time.zeroTime);
		snk.makeQuitTask(Time.theLastTime);
		Command.printPendingTasks();
		Command.doManyTasks(200);
		Link.printStatistics();
		Joint.printStatistics();
		Command.printStatistics();
	}// end of testRoundRobinB

	// this is the test of the demand merge
	// it has two sources
	static public void testDemandMerge() {
		// make the
		Command.reset();
		Component p = new Component(null, "ROOT", true);
		// make the fixed Joints
		SourceJoint srcA = new SourceJoint("srcA", p);
		SourceJoint srcB = new SourceJoint("srcB", p);
		SinkJoint snk = new SinkJoint("snk", p);
		DemandMergeJoint merge = new DemandMergeJoint("mrg", p);

		// wire them up
		JointFactory.makeFIFO(p, srcA, merge, 1);
		JointFactory.makeFIFO(p, srcB, merge, 1);
		JointFactory.makeFIFO(p, merge, snk, 2);

		// exhibit the topology
		p.printTheComponents();
		Joint.checkJointTopology();
		Joint.clearAllJoints();
		Link.clearAllLinks();
		Link.printTopology();
		Joint.printTopology();

		// make initializers and do
		srcA.makeStartTask(Time.zeroTime);
		srcB.makeStartTask(Time.zeroTime);
		snk.makeQuitTask(Time.theLastTime);

		System.out.println("PENDING TASKS ARE");
		Command.printPendingTasks();
		System.out.println("START SIMULATION");
		Command.doManyTasks(200);
		Link.printStatistics();
		Joint.printStatistics();
		Command.printStatistics();
	}

	private class Comp extends Component {
		private int comNumber = 1;

		private Comp(String name, Component parent) {
			super(parent, "undefined for whatever reason");
			String n = (name == null) ? ("C:" + comNumber) : name;
			setName(n);
		}

		public String getTypeString() {
			return "TestComponent";
		}

		public void masterClear() {
			return;
		}

	}

}
