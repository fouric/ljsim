/* Class SinkJoint is a joint with only input links.
 * It drains its input link a fixed number of times ..
 * Written by Ivan on 4 June 2017
 * Updated by Ivan on 14 June 2017
 * Updated by Ivan on 12 August 2017
 * 
 * SinkJoint also holds the quitJoint with it's sorted Joint command to quit.
 */

package ljSim.jointPkg;

import ljSim.actionPkg.SinkAction;
import ljSim.basicA.Time;
import ljSim.commands.Command;
import ljSim.commands.SortedJointCommand;
import ljSim.components.Component;
import ljSim.components.Joint;
import ljSim.components.Link;

public class SinkJoint extends Joint {

	// used to be protected, for whatever reason
	public SinkJoint(String name, Component parent) {
		super(name, parent);
		addAnAction(new SinkAction("drainAll", this));
		return;
	}

	public String getTypeString() {
		return "SinkJoint";
	}

	public void makeQuitTask(Time t) {
		String s = timeHerald(t) + " makes a quit task";
		myMessenger.line(s);
		SortedJointCommand myWakeUp = makeAwakeUpCommand();
		myWakeUp.setSource(null);
		myWakeUp.enQueueMe(t);
	}

	public SortedJointCommand makeAwakeUpCommand() {
		SortedJointCommand c = new WakeUpCommand(this);
		return c;
	}

	/// quit
	public boolean wakeAndDo(Link who) {
		// the Wakeup part
		wakeReport(who);
		if (who != null)
			super.wakeAndDo(who);
		return true;
	}

	private class WakeUpCommand extends SortedJointCommand {
		private WakeUpCommand(Joint j) {
			super(j);
		}

		public void doIT() {
			Joint J = getTarget();
			J.wakeAndDo(getSource());
			return;
		}

		public String getMyType() {
			return ("SortedWakeUpCommand");
		}
	}// end of class WakeUp

	// ---------- topology builders ----------------------
	public void addAnOutputLink(Link lk) {
		super.addAnOutputLink(lk, 0);
	}

}// end of class SinkJoint
