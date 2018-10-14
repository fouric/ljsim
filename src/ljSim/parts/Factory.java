/* Factory.java has a collection of methods to build complex FIFOs *//* IES  17 May 1997 *//* updated 9 September 1997 *//* updated IES 6 June 1997 */package ljSim.parts;import ljSim.basicA.Messenger;import ljSim.basicA.Time;import ljSim.commands.Command;import ljSim.commands.JointCommand;import ljSim.commands.SortedJointCommand;import ljSim.commands.SquareQueue;import ljSim.jointPkg.DemandMergeJoint;import ljSim.jointPkg.JointFactory;import ljSim.jointPkg.OneInOneOutJoint;import ljSim.jointPkg.RoundRobinForkJoint;import ljSim.jointPkg.RoundRobinJoinJoint;import ljSim.jointPkg.RoundRobinJoint;import ljSim.jointPkg.SinkJoint;import ljSim.jointPkg.SourceJoint;import ljSim.components.Component;import ljSim.components.Group;import ljSim.components.Joint;import ljSim.components.Link;public class Factory {	static private Messenger myMessenger = Messenger.createAppropriateMessenger("Factory", 2);	public Factory(Messenger M) {		myMessenger = (M != null) ? M : Messenger.createAppropriateMessenger("Factory", 2);		return;	}	// to test SquareQueue	static public void testQ() {		Command.sq = new SquareQueue(myMessenger);		SortedJointCommand[] ca;		ca = new SortedJointCommand[10];		JointCommand[] cb;		cb = new JointCommand[10];		Joint[] ja;		ja = new Joint[10];		Joint j = new SinkJoint("testJoint", null);		//Link la = new Link("testLink", j);		for (int i = 1; i < 9; i++) {			ca[i] = new SortedJointCommand(j);			ca[i].setTime(new Time(i * 1000));			ja[i] = new OneInOneOutJoint(null, j);			cb[i] = ja[i].makeAwakeUpCommand();			cb[i].setTime(new Time(i * 400));		}		ca[5].setTime(new Time(3500)); // this one is out of order		Command.sq.printPendingTasks();		for (int i = 1; i < 9; i++) {			//Time tt = new Time(i * 1010);			ca[i].enQueueMe();		}		Command.sq.printPendingTasks();		for (int i = 1; i < 9; i++) {			Time tt = new Time(i * 440);			cb[i].enQueueMe(tt);		}		Command.sq.printPendingTasks();	}	/// has no usable	// This method tests a single long FIFO	static public void testFifo() {		Command.reset();		new Group(null); // have to make the top		Component p = Component.getTheParent();		// make the fixed Joints		SourceJoint src = new SourceJoint("src", p);		SinkJoint snk = new SinkJoint("snk", p);		// wire them up		JointFactory.makeFIFO(p, src, snk, 8);		// exhibit the topology		Component.printTheComponents();		Joint.checkJointTopology();		Joint.clearAllJoints();		Link.clearAllLinks();		Link.printTopology();		Joint.printTopology();		// make initializers and do		src.makeStartTask(Time.zeroTime);		snk.makeQuitTask(Time.theLastTime);		Command.printPendingTasks();		Command.doManyTasks(200);		Link.printStatistics();		Joint.printStatistics();		Command.printStatistics();	}	/// out of sequence	// this method connects a roundRobinFork and roundRobin Joint	// connected by three short FIFIs.	public void testTwinFifoS() {		// this is a test of TWO FIFOs in parallel		Command.reset();		new Comp("TOP", null); // have to make the top		Component p = Component.getTheParent();		Component groupA = new Comp("groupA", p);		Component groupB = new Comp("groupB", p);		SourceJoint src = new SourceJoint("src", groupA);		SinkJoint snk = new SinkJoint("snk", groupA);		JointFactory.makeFIFO(groupA, src, snk, 5);		JointFactory.makeFIFO(groupB, src, snk, 3);		Component.printTheComponents();		Joint.checkJointTopology();		Joint.clearAllJoints();		Link.clearAllLinks();		Link.printTopology();		Joint.printTopology();		// OneInOneOutJoint.printTopology();		src.makeStartTask(Time.zeroTime);		Command.doManyTasks(200);		Link.printStatistics();		Joint.printStatistics();		Command.printStatistics();		return;	}	static public void testRoundRobinA() {		// what is this Command.setup()? Command is a class? yes, an abstract class.		// reset initializes the member variable sq of type SquareQueue to be a new SquareQueue object		Command.reset();		// uh, wait, what? where is this going? I don't understand... wouldn't this object be immediately GCed because we're not storing a reference to it anywhere?		// OHHHH...this is necessary to set the "parent" attribute of the "Component" class.		new Group(null); // have to make the top		Component p = Component.getTheParent();		// WAT		System.out.print("current parent is " + p);		// ...why not just `new SourceJoint("src", p);`?		// note: these used to be "SourceJoint.please" instead of "new SourceJoint"		SourceJoint src = new SourceJoint("src", p);		SinkJoint snk = new SinkJoint("snk", p);		RoundRobinForkJoint f = new RoundRobinForkJoint("forker", p);		RoundRobinJoinJoint j = new RoundRobinJoinJoint("joiner", p);		JointFactory.makeFIFO(p, src, f, 2);		JointFactory.makeRR(p, f, j, 3, 2);		JointFactory.makeFIFO(p, j, snk, 2);		Component.printTheComponents();		Joint.checkJointTopology();		Joint.clearAllJoints();		// Link.clearAllLinks();		Link.printTopology();		Joint.printTopology();		// why did we print the link topology twice?		//Link.printTopology();		/// has no usable		src.makeStartTask(Time.zeroTime);		snk.makeQuitTask(Time.theLastTime);		Command.printPendingTasks();		Command.doManyTasks(200);		Link.printStatistics();		Joint.printStatistics();		Command.printStatistics();	}	static public void testRoundRobinB() {		Command.reset();		new Group(null); // have to make the top		Component p = Component.getTheParent();		SourceJoint src = new SourceJoint("src", p);		SinkJoint snk = new SinkJoint("snk", p);		// make the Forker and joiner		RoundRobinJoint f = new RoundRobinJoint("forker", p, "forkType");		f.addActions(1, 3);		RoundRobinJoint j = new RoundRobinJoint("joiner", p, "joinType");		j.addActions(3, 1);		JointFactory.makeFIFO(p, src, f, 2);		JointFactory.makeRR(p, f, j, 3, 2);		JointFactory.makeFIFO(p, j, snk, 2);		Component.printTheComponents();		Joint.printTopology();		Joint.checkJointTopology();		Joint.clearAllJoints();		Link.printStatistics();		// Link.clearAllLinks();		Link.printTopology();		src.makeStartTask(Time.zeroTime);		snk.makeQuitTask(Time.theLastTime);		Command.printPendingTasks();		Command.doManyTasks(200);		Link.printStatistics();		Joint.printStatistics();		Command.printStatistics();	}// end of testRoundRobinB	// this is the test of the demand merge	// it has two sources	static public void testDemandMerge() {		// make the		Command.reset();		new Group(null); // have to make the top		Component p = Component.getTheParent();		// make the fixed Joints		SourceJoint srcA = new SourceJoint("srcA", p);		SourceJoint srcB = new SourceJoint("srcB", p);		SinkJoint snk = new SinkJoint("snk", p);		DemandMergeJoint merge = new DemandMergeJoint("mrg", p);		// wire them up		JointFactory.makeFIFO(p, srcA, merge, 1);		JointFactory.makeFIFO(p, srcB, merge, 1);		JointFactory.makeFIFO(p, merge, snk, 2);		// exhibit the topology		Component.printTheComponents();		Joint.checkJointTopology();		Joint.clearAllJoints();		Link.clearAllLinks();		Link.printTopology();		Joint.printTopology();		// make initializers and do		srcA.makeStartTask(Time.zeroTime);		srcB.makeStartTask(Time.zeroTime);		snk.makeQuitTask(Time.theLastTime);		myMessenger.line("");		myMessenger.line("PENDING TASKS ARE");		Command.printPendingTasks();		myMessenger.line("");		myMessenger.line("START SIMULATION");		Command.doManyTasks(200);		Link.printStatistics();		Joint.printStatistics();		Command.printStatistics();	}	private class Comp extends Component {		private int comNumber = 1;		private Comp(String name, Component parent) {			super(parent);			String n = (name == null) ? ("C:" + comNumber) : name;			setName(n);		}		public String getTypeString() {			return "TestComponent";		}		public void masterClear() {			return;		}	}}