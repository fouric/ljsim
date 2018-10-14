/* Link.java is the  Link */
/* IES 26 April 1997 */
/* rewritten to have only a single command  9 December 2017 */
/* updated by Ivan starting 18 December 2017 - Commands no longer carry TIME*/
/*
  Class Link represents a directed connection between two Joints.
  A Link puts a JointCommand in either mySourceCommand or my SinkCommand.
  Joints put LinkHandle Commands in a list of inputs or a list of outputs.
  
* Each Link attaches to exactly two Joints by giving them its LinkHandle Command.
* Each Joint attaches to arbitrarily many Links in lists 
     called  inputDrainCommands and outputFillCommands 
* It knows its input or output Joint because it via the Joint's wakeUp command.  

* COMMENT added 24 November 2017
* Each link holds two TValues, one at its input and one at its output.
* They must never be missing.
* Comment added 9 December 2017
* A Link has four states: EMPTY, FILLing, FULL, DRAINing.
* It cycles through them in that order.
* It can tell it's state by the content of its two ends
* EMPTY - both ends have null data (with a time stamp)
* FILLing - input end has real data, output end has null data
* FULL - both ends have real data
* DRAINing - output end has null data input end still has real data.
*      Note that FILLing and DRAINing have the same end states.
*      FILLing and DRAINing are distinguished only by time stamps
*We may want links to remember their last state and check circulaton.
*  * INITIALIZATION sets both ends to either full or empty with the same time stamp.  (maybe 0)
*/

package ljSim.components;

import java.util.LinkedList;
import java.util.List;

import ljSim.basicA.Time;
import ljSim.basicA.Messenger;
import ljSim.basicA.TimedValue;
import ljSim.commands.JointCommand;
import ljSim.commands.LinkCommand;

public class Link extends Component {

	protected static Messenger myMessenger = Messenger.createAppropriateMessenger("Link class", 2);
	private static List<Link> theLinks = new LinkedList<Link>();
	private static int linkNumber = 1; // used in nameing Links

	public static int getLinkNumber() {
		return linkNumber;
	}

	private TimedValue myInputTimedValue; // value and time at source end of Link
	private TimedValue myOutputTimedValue; // value and time at sink end of the Link

	public TimedValue getOutput() {
		return myOutputTimedValue;
	}

	public TimedValue getInput() {
		return myInputTimedValue;
	}

	protected int passCount = 0; // number of times this Link passed data
	protected int spaceCount = 0; // number of spaces this Link passed
	protected int wait4data = 0; // total time waiting to be drained
	protected int wait4space = 0; // total time waiting to be filled
	protected String lastAction = null; // EMPTY, FILLing, FULL, DRAINing
	protected Time lastActionTime = null;

	protected Time getOutputTime() {
		return getOutput().getTime();
	}

	protected Time getInputTime() {
		return getInput().getTime();
	}

	// these Commands are the only topology pointers to Joints
	private JointCommand mySinkCommand = null; // do this Joint command when FULL at output
	private JointCommand mySourceCommand = null; // do this Joint command when EMPTY at input

	public JointCommand getSinkCommand() {
		return mySinkCommand;
	} // belongs to a Joint

	public JointCommand getSourceCommand() {
		return mySourceCommand;
	} // belongs to a Joint

	public boolean inputFULL() {
		return myInputTimedValue.hasValue();
	}

	public boolean outputFULL() {
		return myOutputTimedValue.hasValue();
	}

	public boolean inputFirst() {
		Time inputTime = myInputTimedValue.getTime();
		Time outputTime = myOutputTimedValue.getTime();
		if (inputTime.isBefore(outputTime))
			return true;
		return false;
	}

	/// passing data
	public boolean outputFirst() {
		Time inputTime = myInputTimedValue.getTime();
		Time outputTime = myOutputTimedValue.getTime();
		if (outputTime.isBefore(inputTime))
			return true;
		return false;
	}

	public Link(String name, Component parent) {
		super(parent);
		theLinks.add(this);
		String newName = nameMaker(name);
		setName(newName);
		this.masterClear();
		String t = this.getTypeString();
		myMessenger.line("made and cleared Link " + getName() + " of Type " + t + " with parent " + getMyParent().getName());
		return;
	}

	private String nameMaker(String name) {
		// create a name for this Link
		String nn = (name == null) ? "" : "-" + name;
		int n = getLinkNumber();
		String ans = "L:" + n + nn;
		linkNumber++;
		return ans;
	}

	@Override
	public String getTypeString() {
		return "Link";
	}

	@Override
	public void masterClear() {
		myInputTimedValue = new TimedValue(Time.zeroTime, null, "initial emptiness");
		myOutputTimedValue = new TimedValue(Time.zeroTime, null, "initial no output");
		lastAction = computeState();
		// myMessenger.line("masterClear applied to " + getName());
		// myMullerC.masterClear(d, v);
	}// end of masterClear

	protected String computeState() {
		String ans = null;
		if (inputFULL() && outputFULL())
			ans = "FULL";
		if (!inputFULL() && !outputFULL())
			ans = "EMPTY";
		if (!inputFULL() && outputFULL()) {// an impossible case - report error
			myMessenger.error(this.getFullName() + " has impossible state ");
			return null;
		}
		if (ans != null)
			return ans;
		// must be either filling or draining
		if (outputFirst())
			ans = "FILLing";
		if (inputFirst())
			ans = "DRAINing";
		if (ans == null) {// can't decide on direction
			myMessenger.error(this.getFullName() + " has undecidable state ");
			return null;
		}
		return ans;
	}

	// inidilize to FULL or EMPTY according to Value field in parameter
	public void initializeWith(TimedValue tv) {
		myInputTimedValue = tv;
		myOutputTimedValue = tv;
		lastAction = getStateString();
		myMessenger.line(getTypeAndName() + " initialized " + lastAction);
	}

	public String stateString() {
		String ss = computeState();
		String ans = getFullName() + " computed state is " + ss + " stored state is " + lastAction;
		return ans;
	}

	public Time lastTime() {
		Time ot = myOutputTimedValue.getTime();
		Time it = myInputTimedValue.getTime();
		return Time.latestOf(ot, it);
	}

	public Time becameFullAt() {
		// do a check
		String ss = computeState();
		if (!ss.equals("FULL")) {
			return null;
			// myMessenger.error(getFullName() + " not FULL when asked");
			// myMessenger.error(stateString());
		}
		Time t = myOutputTimedValue.getTime();
		return t;
	}

	public Time becameEmptyAt() {
		// do a check
		String ss = computeState();
		if (!ss.equals("EMPTY")) {
			return null;
			// myMessenger.error(getFullName() + " not EMPTY when asked");
			// myMessenger.error(stateString());
		}
		Time t = myInputTimedValue.getTime();
		return t;
	}

	// -------The action methods ------------
	private void doMe() {
		// theck the state of this Link
		String state = computeState();
		if (state.equals("FILLing") && lastAction.equals("EMPTY")) {
			passData();
			return;
		}
		if (state.equals("DRAINing") && lastAction.equals("FULL")) {
			passSpace();
			return;
		}
		// this action can't be happening
		myMessenger.error(getFullName() + " out of sequence action ");
		myMessenger.error(stateString());
		return;
	}

	/// has no useable action
	private void passData() {
		int passDelay = 33; // the time for this Link to pass data

		// collect statistics
		passCount++; // note number of times this link passes data

		TimedValue tv = myInputTimedValue;
		Time myTime = tv.getTime();
		int diff = Time.between(lastActionTime, myTime);
		wait4data = wait4data + diff;

		lastActionTime = myTime;
		lastAction = "FULL";

		// make a report
		String s = timeHerald(myTime) + " passing data count " + passCount + " value "
				+ myInputTimedValue.valueString();
		myMessenger.line(s);

		// pass the data
		myOutputTimedValue = tv.delayedBy(passDelay);
		mySinkCommand.doIT();
		return;
	}

	private void passSpace() {
		int drainDelay = 22; // the time to pass a space backwards

		// collect statistics
		spaceCount++; // note number of times it passes spaces

		TimedValue tv = myOutputTimedValue;
		Time myTime = tv.getTime();
		int diff = Time.between(lastActionTime, myTime);
		wait4space = wait4space + diff;

		lastActionTime = myTime;
		lastAction = "EMPTY";

		String s = timeHerald(myTime) + " passing space " + spaceCount;
		myMessenger.line(s);

		// pass space to input
		myInputTimedValue = tv.delayedBy(drainDelay);
		mySourceCommand.doIT();
	}

	// The LinkHandle command causes the link to go thru its states
	private class LinkHandle extends LinkCommand {
		// constructor
		private LinkHandle(Link L) {
			super(L);
		}

		public String getMyType() {
			return ("LinkHandle");
		}

		// if TimedValue had Data, put in myOutputValue
		// if TimedValue has no Data, put it in myInputValue
		public void grabIT(TimedValue tv) {
			// could put a check in here
			if (tv.getValue() != null)
				myInputTimedValue = tv;
			else
				myOutputTimedValue = tv;
			return;
		}

		/*
		public TimedValue getLaterValue() {
			return TimedValue.laterOf(myOutputTimedValue, myInputTimedValue);
		}
		*/

		// this method causes the link to do its thing, ending up FULL or EMPTY
		public void doIT() {
			Link L = getTarget();
			// Link is supposed to be now (simulation time) EMPTY
			L.doMe();
			return;
		}
	} // end of DoLink Class

	// -------------- topology builders -------------

	// use this to attach a jointCommand as my output, e.g. my Sink
	public void attachJointCommandForOutput(JointCommand J) {
		if (mySinkCommand != null)
			myMessenger.error("double attach Link output");
		else {
			mySinkCommand = J;
			mySinkCommand.setSource(this);
		}
	}// end of attachJointCommandForOutput

	public void attachJointCommandForInput(JointCommand J) {
		if (mySourceCommand != null)
			myMessenger.error("double attach Link input");
		else {
			mySourceCommand = J;
			mySourceCommand.setSource(this);
		}
	}// end of attachJoint

	public LinkHandle makeAhandle() {
		return new LinkHandle(this);
	}

	// ------------------ static service routines for all Links
	// ---------------------
	static public void clearAllLinks() {
		int num = theLinks.size();
		myMessenger.line("");
		myMessenger.line("starting to clear " + plural(num, " Link"));
		for (Component CC : theLinks)
			CC.masterClear();
		myMessenger.line("clearAllLinks is done ");
	}// end of clearAllLinks

	static public void printTopology() {
		int num = theLinks.size();
		myMessenger.line("");
		myMessenger.line("Printing topology of " + plural(num, " Link"));
		for (Link L : theLinks)
			L.printMyTopology();
		myMessenger.line("printTopology of Links is done ");
		myMessenger.line("");
	}// end of printTopology

	public void printMyTopology() {
		Joint in = (Joint) mySourceCommand.getTarget();
		Joint out = (Joint) mySinkCommand.getTarget();
		myMessenger.line(getName() + " gets input from " + in.getName());
		myMessenger.line(getName() + " sends output to " + out.getName());
	}// end of printMyTopology

	private String getStateString() {
		if (myInputTimedValue.getValue() != null) {// input has data
			if (myOutputTimedValue.getValue() != null)
				return "FULL";
			return "FILLing";
		} else {
			if (myOutputTimedValue.getValue() != null)
				return "DRAINing";
			return "EMPTY";
		}
	}

	private String ratio(int a, int b) {
		String ans;
		if (b == 0)
			return "infinity";
		ans = Integer.toString(a / b);
		return ans;
	}

	public void printMyStatistics() {
		String s, ss;
		s = getFullName() + " passed " + plural(passCount, " item") + " and " + plural(spaceCount, " space");
		ss = " and is " + getStateString();
		myMessenger.line(s + ss);
		s = "    spaceWait  total= " + wait4space + " average= " + ratio(wait4space, spaceCount);
		ss = "    dataWait  total= " + wait4data + " average= " + ratio(wait4data, passCount);
		myMessenger.line(s);
		myMessenger.line(ss);
	}// end of printMyStatistics

	static public void printStatistics() {
		int num = theLinks.size();
		myMessenger.line("");
		myMessenger.line("Printing statistics of " + plural(num, " Link"));
		for (Link L : theLinks)
			L.printMyStatistics();
		myMessenger.line("printStatistics of Links is done ");
	}

	// a service method to list the names of the links in a List of LinkCommands
	static public String getNamesOfLinks(List<LinkCommand> cc) {
		String ans = "";
		boolean first = true;
		for (LinkCommand C : cc) {
			if (!first)
				ans = ans + ", ";
			ans = ans + C.getTargetName();
			first = false;
		}
		return ans;
	}

}// end of class Link
