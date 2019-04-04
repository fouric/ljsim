/* the Action class actually does the work in a joint
* every Action has three methods: 
*    guard() returns the Time at which the action can be done (or null if it can't)
*       the Action time is recorded for the action
*    grab(Time) is essentially the fire time for the action
*    fire() does the action and returns true, 
*       or returns false if the action can't be done, e.g. used up firings.
 * Every time a Joint is woken up it checks all its actions to see if any Guard says to go ahead. 
 * if Only one says it can fire, fire that one.
 * otherwise do the earliest one. 
 * When an Action fires is does ALL of the I/O in the form:
 *     grab grab grab fill fill drain.  All grabs must come before any fills.
 *     
 */
/* Written by Ivan 12 August 2017 */

package ljSim.actionPkg;

import ljSim.basicA.TimedValue;
import ljSim.basicA.Time;
import ljSim.commands.LinkCommand;
import ljSim.components.Joint;
import ljSim.components.Link;

abstract public class Action {

	private Joint myJoint = null;
	protected String actionName = null;

	public String getName() {
		return actionName;
	}
	// time when this action fired last
	// protected Time lastFireTime= Time.zeroTime;

	private int circumference;

	protected int getCircumference() {
		return circumference;
	}

	protected void setCircmference() {
		Joint J = this.getMyJoint();
		int d = J.getInputDrainCommands().size();
		int f = J.getOutputFillCommands().size();
		int c = d > f ? d : f;
		circumference = (c - 1);
	}

	public Joint getMyJoint() {
		return myJoint;
	}

	public String getMyName() {
		return actionName;
	}

	public String getJointName() {
		return myJoint.getName();
	}

	public String getCombinedName() {
		return getJointName() + "-" + getMyName();
	}

	// myGuardTime is a cache for the guard time
	// because grab kills out ability to compute a new guard time.
	protected Time myGuardTime = null;

	public void setMyGuardTime(Time t) {
		myGuardTime = t;
	}

	public Time getMyGuardTime() {
		return myGuardTime;
	}

	private TimedValue myValue = null;

	protected void setMyValue(TimedValue v) {
		myValue = v;
	}

	public TimedValue getMyValue() {
		return myValue;
	}

	// The guard should return a time if all is OK, null otherwise
	// leaves that time in myGuardTime
	abstract public Time guard();

	// grabs the near end of the Links, leaving their new value there
	// this is where the action computes the values for output and input
	abstract public void grab(Time t);

	// fire returns true if all is OK, false otherwise
	abstract public boolean fire();

	// to make whatever connectons are necessary
	abstract public void initialize();// must overwrite this one

	// the constructor
	protected Action(String name, Joint parent) {
		actionName = name;
		myJoint = parent;
	}

	// ------------------ service methods to be used by sub-classes as needed
	// ---------

	// broadcast actions need to know when all their links can do something
	// other joints, e.g. round robin joints may be more selective.
	protected LinkCommand getInCmd(int i) {
		return getMyJoint().getInCmd(i);
	}

	protected LinkCommand getOutCmd(int i) {
		return getMyJoint().getOutCmd(i);
	}

	protected TimedValue getInputValue(int i) {
		return getMyJoint().getInLink(i).getOutput();
	}

	protected Time guardInputLink(int linkNum) {
		return getMyJoint().getInLink(linkNum).becameFullAt();
	}

	protected Time guardOutputLink(int linkNum) {
		return getMyJoint().getOutLink(linkNum).becameEmptyAt();
	}

	protected Time guard(int in, int out) {
		Time tin = guardInputLink(in);
		Time tout = guardOutputLink(out);
		Time ans = Time.lastOf(tin, tout);
		return ans;
	}

	// find the time at which the last input filled
	protected Time guardInputLinks() {
		Time doneTime = null;
		for (LinkCommand C : myJoint.getInputDrainCommands()) {
			Link L = C.getTarget();
			//TimedValue tv = L.getOutput();
			Time fullTime = L.becameFullAt();
			if (fullTime == null) {// print who is not full
				System.out.println("Link " + L.getFullName() + " is not full");
				return null;// someone wasn't ready
			}
			// the Link is full, but at what time?
			doneTime = fullTime.butAfter(doneTime);
		}
		return doneTime;
	}

	// find the time at which the last output drained
	protected Time guardOutputLinks() {
		Time doneTime = null;
		for (LinkCommand C : myJoint.getOutputFillCommands()) {
			Link L = C.getTarget();
			Time emptyTime = L.becameEmptyAt();
			if (emptyTime == null) {// print who was not empty
				System.out.println("Link " + L.getFullName() + " is not empty");
				return null;
			}
			// it's empty, but at what time did it become so
			doneTime = emptyTime.butAfter(doneTime);
		}
		return doneTime;
	}

	// a generalized guard between an input and an output LinkCommands
	// test to see when the last of in is FULL and out is EMPTY
	static protected Time guard(LinkCommand in, LinkCommand out) {
		if (in == null || out == null)
			return null; // incomplete
		Time from = in.linkBecameFullAt();
		Time to = out.linkBecameEmptyAt();
		Time ans = Time.lastOf(from, to); // null if either is null
		return ans;
	}

	// This methods puts a space value into an input link
	static protected void grabIn(LinkCommand in, TimedValue tv, int delay) {
		TimedValue use = tv.drained().delayedBy(delay);
		in.grabIT(use);
	}

	// This method puts a value into an output link
	static protected void grabOut(LinkCommand out, TimedValue tv, int delay) {
		TimedValue use = tv.delayedBy(delay);
		out.grabIT(use);
	}

	// this computes values for grabbing the Links indicated by from and to
	protected void grab(LinkCommand in, LinkCommand out, Time t) {
		int fillDelay = 6;
		int drainDelay = 9;
		// Find when the action is possible (if at all)
		Time actTime = guard(in, out);
		if (actTime == null)
			return;
		actTime = actTime.butAfter(t);
		TimedValue tv = in.getTarget().getOutput(); // this is the value to move
		TimedValue tvOut = tv.butAfter(actTime);
		TimedValue tvIn = tvOut;
		tvOut = tvOut.delayedBy(fillDelay);
		tvIn = tvIn.delayedBy(drainDelay);
		tvIn.drainMyValue();
		in.grabIT(tvIn);
		out.grabIT(tvOut);
		return;
	}

	// this one fills or drains the specified LinkCommand at time t with delay
	protected void grab(LinkCommand lc, TimedValue tv, Time t) {
		int fillDelay = 6;
		int drainDelay = 9;
		int delay;
		delay = tv.hasValue() ? fillDelay : drainDelay;
		TimedValue tva = tv.delayedBy(delay);
		lc.grabIT(tva);
	}

	protected void grab(int n, Time t, TimedValue tv, int delay) {
		TimedValue ans = tv.butAfter(t).delayedBy(delay);
		if (tv.hasValue())
			this.getMyJoint().getOutCmd(n).grabIT(ans);
		else
			this.getMyJoint().getOutCmd(n).grabIT(ans);
	}

	public void fireMessage(Time t) {
		Joint J = this.getMyJoint();
		String s = J.timeHerald(t) + " Action " + getMyName() + " fires with use count " + J.getUseCount();
		System.out.println(s);
	}

}// end of class Action
