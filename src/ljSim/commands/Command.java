/* 
 Command is the base type of Command. 
 */
/* updated by Ivan 19 August 2017 */
/* updated by Ivan 18 November 2017 */
/* updated by Ivan 18 December 2017 */
/* updated by Ivan 04 January 2018 */

/* A Command has a target which is the Component that will DO that command
 * A LinkComand's target is a Link, a JointCommand's target is a Joint.
 * Links issue a LinkCommand at topology setup time so a Joint can invoke the Link.
 * Joints issue a JointCommean at topology setup time so a Link can wake them up.
 *      Joint commands point back to their source, the Link that will use them
 *             so the Joint knows who woke it up.
 *      This pointer is also set up at topology time.
 *      
 * UnsortedCommand is the parent class for both LinkCommand and JointComand.
 * UnsortedCommand provides methods to deal with a Deque (FIFO) of unsorted commands
 * SortedJointCommand manages a SoortedSet of SortedJointComands called theQueue
 * StartJoints place one or more starter Commands at timeAero a theQueue
 * Each SortedJointCommand, holds and manages a Deque called doBefore
 *      that holds UnsortedCommands that must be done before that SortedJointCommand.
 * You must exhaust doBefore before executing the SortedJointCommand.
 * 
 * Joints issue JointCommands during topological setup when a Link wants one
 * A Joint Command also has a source, the Link that called for the Command to be done.
 * The source Link is recorded in the JointCommand as the topology is built.
 * Each Command must be able to get a Time so it can be put in its place in the list.
 * For LinkCommands, the time is the last time of it's two ends.
 * For a JointCommand it's the guard time of the Joint.
 * It's time may be set when it's called.
 * 
 * Command
 *       UnsortedCommand
 *               LinkCommand     JointCommand
 *                                       SortedJointCommand
 *       There are no sorted Link Commands. 

 * Every Command uses the final void method DO() with no return value.
 * A LinkCommand acts, either: 
 *      calling its Joints in sequence or enQueue them - for different simulation behavior.
 * A JointCommand merely wakes up the Joint - 
 *      The Joint has a guard to decide when and if it can act.
 * SortedJointCommands always to onto the queue and are taken off in Time sequence.
 * 
 * A SortedJointCommand gets its time from the Joint's guard.

 * The DO method for a Joint has three parts:
 * 1) call the target's guard method.
 *      guard() returns the earliest time at which some action could happen, 
 *      or null if no action is possible.
 *      A single action is possible when it's last required input arrives.
 *      If there are several possible actions, the returned Time is the earliest of them.
 *      and the Joint must remember which action is first
 *      
 * 2) If guard says no action is possible, it is OK to drop the command
 *      because some other Link will wake the Joint again.
 * 
 * 3) call the target's doIT method, passing in the time you got from the guard.
 *      the doIT method must grab all involved links and then fire the joint's action
 *      doIT returns false if there's an error condition.
 * Targets of Commands must have those methods but they can have unique names.
 *      
 */

///About to
package ljSim.commands;

import ljSim.basicA.Time;
import ljSim.components.Component;

abstract public class Command implements Comparable<Command> {
	private Component target = null;
	// private Component source = null;

	private Time myTime;

	public Time getTime() {
		return myTime;
	}

	public void setTime(Time t) {
		myTime = t;
	}

	public static SquareQueue sq = new SquareQueue();

	public static void reset() {
		// what is a SquareQueue, and why don't we just set this variable inside the class definition instead of in this init function?
		/* The SquareCueue class holds a two-dimensional queue called theQueue.
		 * The first dimension uses a NavigableSet to sort SortedJointCommands by Time
		 * In the second dimension the SquareQueue is unsorted:
		 * Each SortedJointCommand has a Queue of UnsortedCommands called doBefore 
		 * The first dimension sorts time into unsorted epochs of unsorted commands. 
		 * This class contains the stuff to make that happen.
		 * Use an instance of this class as a command queue.
		 */
		// wait, what's a "two-dimensional queue"?
		sq = new SquareQueue();
	}

	/// and can do
	// outside users have to call doIT to use a command
	// different kinds of commands may do this differently
	abstract public void doIT(); // will differ for different subComands
	// enQueue adds this Command to its proper queue
	// returns null if it's already there.

	abstract protected boolean enQueueMe(Time t);

	abstract protected String getMyType(); // e.g. {return "RawCommand";}

	public int compareTo(Command c) {
		int myTime;
		int hisTime;
		Time t = getTime();
		myTime = t.getMyTimeNum();
		hisTime = c.getTime().getMyTimeNum();
		if (myTime < hisTime) {
			return -1;
		} else if (myTime > hisTime) {
			return 1;
		}
		// times are equal, distinguish by name
		String s = this.getTargetName();
		myTime = s.hashCode();
		s = c.getTargetName();
		hisTime = s.hashCode();
		if (myTime < hisTime)
			return -1;
		if (myTime > hisTime)
			return 1;
		return 0;
	}

	public String getName() {
		// get an identifier for this Command
		return getMyType() + "-" + getTargetName();
	}

	// DO is called by doOneCommand only
	// replace only if you must use the Task queue
	private final void DO() {
		doIT();
		return;
	}

	protected Command(Component J) {
		target = J;
	}

	protected Component getTarget() {
		return target;
	}

	public final String getTargetName() {
		return ((target == null) ? "NULL" : target.getName());
	}

	// -------------- The rest of this is for queuing Commands ----------

	public static void quit() {
		String s = "------- Unable to find a task to do so I quit";
		System.out.println(s);
		System.out.println("end of simulation");
		System.out.println("");
		// System.exit(0);
	}

	// get a task from the early Queue unless empty
	// if EarlyQueue is empty try the lateQueue
	// if nothing to do return null
	protected static Command getAtask() {
		Command task;
		task = sq.getNextCommand();
		return task;
	}

	// returns true if a task was done, false if no more to do
	static public boolean doOneTask() {
		String s;
		Command task = getAtask();
		if (task == null) {
			quit();
			return false;
		}
		s = "" + task.getTime().getMyTimeNum() + " About to do " + task.getName() + ". It and " + sq.getCommandsPending() + " other Commands remain to do";
		System.out.println(s);
		task.DO(); // if the task can't be done it will be dropped
		return true;
	}

	// do N Chores or until end of list, return true if more to do
	public static boolean doManyTasks(int N) {
		int i = (N < 0) ? (-N) : N;
		for (; i > 0; i--) {
			boolean b = doOneTask();
			if (b == false) {
				System.out.println("The Task list is empty!!!");
				return false;
			}
		}
		return true;
	}

	public static boolean doAllTasks() {
		return false;
	}

	// ------------------------ service methods ---------------------
	public void printMe() {
		String s = this.getName() + myTime.atTimeString();
		System.out.println(s);
	}

	static public void printPendingTasks() {
		sq.printPendingTasks();
	}

	static public void printStatistics() {
		sq.printStatistics();
	}

}
