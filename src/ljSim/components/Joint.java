/* A Joint is the most general type of Joint*/
/* updated by Ivan 11 August 2017 */
/* updated by Ivan 10 December 2017 */
/* updated by Ivan starting 18 December 2017 - Commands no longer carry TIME*/
/*
 * A Joint has a list of one or more Actions
 * A Joint has a list of zero or more input LinkCommands pointing to Links
 * A Joint has a list of zero or more output LinkCommands pointing to Links
 * Each input or output Link knows it's attached to this Joint because
 * the Link has the Joint's WakeUp command, and the Command's target is this joint.
 * the Joint has only a LikHandle from each Link that it touches.
 * The joint's Actions have the indices of the LinkHandleS they need,
 * Joints avoid firing - only Actions guard, grab and fire.
 * Joints merely hold topology.
 */

package ljSim.components;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ljSim.actionPkg.Action;
import ljSim.basicA.Time;
import ljSim.commands.JointCommand;
import ljSim.commands.LinkCommand;

public class Joint extends Component {

	// a list of all Joints
	private static List<Joint> theJoints = new LinkedList<Joint>();
	private static int jointNumber = 1; // used for naming Joints

	public int getJointNumber() {
		return jointNumber;
	}

	private List<LinkCommand> inputDrainCommands = null;
	private List<LinkCommand> outputFillCommands = null;
	protected List<Action> actions = null;

	public void addAnAction(Action a) {
		actions.add(a);
	}

	// protected int bestActionIndex= Integer.MAX_VALUE;
	private int myWakeCount = 0;

	public int getWakeCount() {
		return myWakeCount;
	}

	public void incWakeCount() {
		myWakeCount++;
	}

	private int myUseCount = 0;

	public int getUseCount() {
		return myUseCount;
	}

	protected void incUseCount() {
		myUseCount++;
	}

	// accessor methods
	public List<LinkCommand> getInputDrainCommands() {
		return inputDrainCommands;
	}

	public LinkCommand getInCmd(int i) {
		return inputDrainCommands.get(i);
	}

	public Link getInLink(int i) {
		return inputDrainCommands.get(i).getTarget();
	}

	public List<LinkCommand> getOutputFillCommands() {
		return outputFillCommands;
	}

	public LinkCommand getOutCmd(int i) {
		return outputFillCommands.get(i);
	}

	public Link getOutLink(int i) {
		return outputFillCommands.get(i).getTarget();
	}

	// peculiar that a join needs masterClear because it's supposed to be devoid of
	// state
	// but Joints keep a use count for simulation statistics
	@Override
	public void masterClear() {
		myUseCount = 0;
		System.out.println("masterClear applied to " + getName());
		System.out.println("initilizing " + actions.size() + " actions.");
		for (Action a : actions)
			a.initialize();
		return;
	}

	@Override
	public String getTypeString() {
		return "Joint";
	}

	private String nameMaker(String name) { // create a name for this joint
		String nn = (name == null) ? "" : "-" + name;
		int n = getJointNumber();
		String ans = "J:" + n + nn;
		jointNumber++;
		return ans;
	}

	// Constructor
	public Joint(String name, Component parent) {
		super(parent, name);
		theJoints.add(this);
		String newName = nameMaker(name);
		setName(newName);
		inputDrainCommands = new LinkedList<LinkCommand>();
		outputFillCommands = new LinkedList<LinkCommand>();
		actions = new ArrayList<Action>(4);
		String t = this.getTypeString();
		String p = "Component " + getName() + " has no parent ";
		Component pp = getMyParent();
		if (pp != null) {
			String pn = pp.getName();
			p = ("made Joint " + getName() + " of Type " + t + " with parent " + pn);
		}
		System.out.println(p);
	}

	// ---------- topology builders ----------------------

	// Joints allowed a limited set of output Links must use this method
	protected void addAnOutputLink(Link lk, int max) {
		if (getOutputFillCommands().size() > max)
			System.err.println("A " + getFullName() + " may have at most " + max + " data outputs");
		else
			addOutputLink(lk);
	}

	public void addAnOutputLink(Link lk) {
		addOutputLink(lk);
	}

	private void addOutputLink(Link lk) {
		LinkCommand fc = lk.makeAhandle();
		outputFillCommands.add(fc);
		JointCommand c = makeAwakeUpCommand();
		lk.attachJointCommandForInput(c);
	}

	// Joints allowed a limited set of input Links must use this method
	protected void addAnInputLink(Link lk, int max) {
		if (getInputDrainCommands().size() > max)
			System.err.println("A " + getFullName() + " may have at most " + max + " data inputs");
		else
			addInputLink(lk);
	}

	public void addAnInputLink(Link lk) {
		addInputLink(lk);
	}

	private void addInputLink(Link lk) {
		LinkCommand dc = lk.makeAhandle();
		inputDrainCommands.add(dc);
		JointCommand c = makeAwakeUpCommand();
		lk.attachJointCommandForOutput(c);
	}

	protected List<Time> getActionTimes() {
		List<Time> ans = new ArrayList<Time>(3);
		for (Action a : actions) {
			Time at = a.guard(); // caches its guard time
			ans.add(at);
		}
		return ans;
	}

	protected int findEarliestActionIndex() {
		List<Time> theTimes = getActionTimes();
		int bestActionIndex = Time.earliestIndex(theTimes);
		return bestActionIndex;// -1 if it can't be done
	}

	protected Time wakeReport(Link who) {
		Time t = null;
		String s = "the system";
		if (who != null) {
			incWakeCount();
			t = who.lastTime();
			s = who.getFullName();
		}
		String ss = timeHerald(t) + " wake up " + myWakeCount + " by " + s;
		System.out.println(ss);
		return t;
	}

	public boolean wakeAndDo(Link who) {
		// the Wakeup part
		Time t = wakeReport(who);
		// List<Time> theTimes= getActionTimes();

		// find the action to do(if any)
		int earliestActionIndex = findEarliestActionIndex();
		if (earliestActionIndex < 0) {
			String ss = timeHerald(t) + " has no useable action";
			System.out.println(ss);
			return false;
		}

		// we have an action to do
		Action bestAction = actions.get(earliestActionIndex);
		Time bestTime = bestAction.getMyGuardTime();

		// now do the action
		incUseCount();
		String f = timeHerald(bestTime) + ":" + bestAction.getMyName() + " fires";
		System.out.println(f);
		bestAction.grab(bestTime);
		bestAction.fire();
		return true;
	}

	public JointCommand makeAwakeUpCommand() {
		JointCommand c = new WakeUpCommand(this);
		return c;
	}

	private class WakeUpCommand extends JointCommand {
		private WakeUpCommand(Joint j) {
			super(j);
		}

		public void doIT() {
			Joint J = getTarget();
			J.wakeAndDo(getSource());
			return;
		}

		public String getMyType() {
			return ("WakeUpCommand");
		}
	}

	public void printMyTopology() {
		String name = getName();
		int in = inputDrainCommands.size();
		int out = outputFillCommands.size();
		int a = actions.size();

		String s = name + " has " + plural(in, " input") + ", " + plural(out, " output") + " and "
				+ plural(a, " action");
		System.out.println(s);
		return;
	}

	// to check that all my links connect to me
	protected boolean checkMyTopology() {
		boolean ok = true;
		for (LinkCommand C : inputDrainCommands) {
			Link L = C.getTarget();
			JointCommand c = L.getSinkCommand();
			Joint t = c.getTarget();
			if (t != this) {
				System.out.println(L.getName() + " output fails to point to " + this.getName());
				ok = false;
			}
		}
		for (LinkCommand C : outputFillCommands) {
			Link L = C.getTarget();
			JointCommand c = L.getSourceCommand();
			Component t = c.getTarget();
			if (t != this) {
				System.out.println(L.getName() + " input fails to point to " + this.getName());
				ok = false;
			}
		}
		return ok;
	}

	// check that there are at least this many inputs and outputs
	protected boolean checkEnoughLinks(int minIn, int minOut) {
		boolean ans = true;
		if (getInputDrainCommands().size() < minIn) {
			ans = false;
			System.err.println("A " + getFullName() + " must have at least " + minIn + " data inputs");
		}
		if (getOutputFillCommands().size() < minOut) {
			ans = false;

			System.err.println("A " + getFullName() + " must have at least " + minOut + " data outputs");
		}
		return ans;
	}

///firing with useCoun 
	public void printMyStatistics() {
		System.out.println(getFullName() + " fired " + getUseCount() + " times but woke " + getWakeCount() + " times");
	}

	// service to print for all Joints
	static public void clearAllJoints() {
		int num = theJoints.size();
		System.out.println("");
		System.out.println("starting to clear " + plural(num, " Joint"));
		for (Component CC : theJoints) {
			CC.masterClear();
		}
		System.out.println("clearAllJoints done ");
	}

	static public void checkJointTopology() {
		boolean ok = true;
		int num = theJoints.size();
		System.out.println("");
		System.out.println("checking topology of " + plural(num, " Joint"));
		List<Joint> test = theJoints;
		for (Joint JT : test)
			ok = ok && JT.checkMyTopology();
		String okS = ok ? "topology is OK" : "topology is bad";
		System.out.println(okS);
	}

	static public void printTopology() {
		int num = theJoints.size();
		System.out.println("");
		System.out.println("Printing topology of " + plural(num, " Joint"));
		List<Joint> test = theJoints;
		for (Joint JT : test)
			JT.printMyTopology();
		System.out.println("printTopology of Joints is done ");
		System.out.println("");
	}

	static public void printStatistics() {
		int num = theJoints.size();
		int wakeUps = 0;
		int uses = 0;
		System.out.println("");
		System.out.println("Printing statistics of " + plural(num, " Joint"));
		for (Joint J : theJoints) {
			wakeUps = wakeUps + J.getWakeCount();
			uses = uses + J.getUseCount();
			J.printMyStatistics();
		}
		int ratio = 1000000;
		if (wakeUps != 0)
			ratio = 100 * uses / (wakeUps + 1);
		System.out.println("All Joints fired " + uses + " times but woke " + wakeUps + " times.  Use to wakup ratio is "
				+ ratio + "%");
		// printDelays();
		System.out.println("printStatistics of Joints is done ");
		System.out.println("");
	}

}
