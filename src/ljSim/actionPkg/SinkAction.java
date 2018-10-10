/* The SinkAction class checks that all inputs are full
 * and upon firing drains them */
/* Written by Ivan 13 August 2017 */

package ljSim.actionPkg;

import ljSim.basicA.Time;
import ljSim.basicA.TimedValue;
import ljSim.commands.LinkCommand;
import ljSim.components.Joint;
import ljSim.components.Link;

public class SinkAction extends Action {

	// sinkAction accepts whatever it's given when it's given
	private int allowedFirings = 10;
	private int sinkDelay = 8;

	public void initialize() {
		return;
	}

	// constructor
	public SinkAction(String name, Joint parent) {
		super(name, parent);
	}

	// a Sink is ready if ANY input Link is FULL
	// compute the time at which the LAST ready input became ready
	public Time guard() {
		Joint J = getMyJoint();
		String s = getCombinedName();
		// ready is the time at which the last input became full
		Time ready = null;
		// are there any drainable input Links?
		for (LinkCommand C : J.getInputDrainCommands()) {
			Link L = C.getTarget();
			Time t = L.becameFullAt();
			if (t == null)
				break;
			ready = t.butAfter(ready);// ok for ready to be initially null
		}
		if (ready == null) {
			s = s + " has no input ready to drain";
			myMessenger.line(s);
			return null;
		}
		// ready holds the earliest that the sink's inputs allow it to fire
		if (J.getUseCount() >= allowedFirings) {
			s = s + " has exceeded " + allowedFirings + " allowed firings";
			myMessenger.line(s);
			return null;
		} // ready contains the earliest it can fire
		setMyGuardTime(ready);
		return ready;
	}

	public void grab(Time t) {
		// Find when the action is possible (if at all)
		Joint J = getMyJoint();
		Time actTime = this.getMyGuardTime();
		actTime = actTime.butAfter(t);
		// get the Link commands
		TimedValue tv = TimedValue.please(t, null, "sink drain");
		for (LinkCommand C : J.getInputDrainCommands()) {
			C.grabIT(tv);
		}
		return;
	}

	public boolean fire() {// fire drains any input Links that are FULL
		Joint J = getMyJoint();
		Time t = getMyGuardTime();
		String s = J.timeHerald(t) + " firing with useCount " + J.getUseCount() + " of " + allowedFirings + " allowed";
		myMessenger.line(s);
		// drain any input link that is FULL
		for (LinkCommand C : J.getInputDrainCommands())
			C.doIT();

		/*
		 * { Link L= C.getTarget(); Time x= L.becameFullAt(); if(x != null) C.doIT(); }
		 */
		return true;
	}

}// end of class SinkAction
