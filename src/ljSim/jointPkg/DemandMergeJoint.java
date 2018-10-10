/* A DemandMerge Joint involves arbitration. */
/* Written by IES 20 August 2017 */
/* upgraded by Ivan on 15 November 2017 */
/* upgraded by Ivan on 7 January 2018 */

/* The DemandMerge Joint can accept at most two inputs (A,B) and one output Link (C).
 * It as two CopyActions A -> C and B -> C.
 * The DemandMerge Joint gets its wakeUp commands in simulandum time sequence
 *     because its WakeUp commands are sorted
 * Because it gets inputs on A and B in time-sequential order
 *    it merely does whichever action is available first. 
 * What about inputs at the same time?
 *   It is free to serve either tied input after both have been served.
 *   It is free of obligation after both have been served.
 */

package ljSim.jointPkg;

/*   // the index number of the input link last served
// this should remain unused
private int indexOfLastStrike= 0;
private int numFire0only;
private int numFire1only;
private int numFire0contested;
private int numFire1contested;
*/

import ljSim.actionPkg.Action;
import ljSim.actionPkg.CopyAction;
import ljSim.basicA.Time;
import ljSim.commands.SortedJointCommand;
import ljSim.components.Component;
import ljSim.components.Joint;
import ljSim.components.Link;

///useable action
public class DemandMergeJoint extends Joint {

	// private int contestedActions = 0;

	public static DemandMergeJoint please(String name, Component parent) {
		return new DemandMergeJoint(name, parent);
	}

	protected DemandMergeJoint(String name, Component parent) {
		super(name, parent);
		addAnAction(CopyAction.please("copyZero", this, 0, 0));
		addAnAction(CopyAction.please("copyOne", this, 1, 0));
	}

	public String getTypeString() {
		return "DemandMerge";
	} // returns the type of this component

	private class WakeUp extends SortedJointCommand {
		// constructor
		private WakeUp(Joint j) {
			super(j);
		}

		public String getMyType() {
			return ("WakeUpCommand");
		}

	}// end of class WakeUp
///guard OK

	@Override
	// Only two possible actions, so decision is simpler than for general Joint
	// this type of joint needs to know about contested uses
	public boolean wakeAndDo(Link who) {
		// the Wakeup part
		Time t = wakeReport(who);
		// record or clear the input times
		// getActionTimes();

		int earliestActionIndex = findEarliestActionIndex();
		if (earliestActionIndex < 0) {
			String ss = timeHerald(t) + " has no useable action";
			myMessenger.line(ss);
			return false;
		}
		Action bestAction = actions.get(earliestActionIndex);
		Time bestTime = bestAction.getMyGuardTime();
		String ss = timeHerald(bestTime) + " and can do " + bestAction.getMyName();
		myMessenger.line(ss);

		// now do the action
		incUseCount();
		String f = timeHerald(bestTime) + ":" + bestAction.getMyName() + " fires";
		myMessenger.line(f);
		bestAction.grab(bestTime);
		bestAction.fire();
		return true;
	}// end of wakeAndDo

	// ---------- topology builders ----------------------

	public void addAnInputLink(Link L, String s) {
		if (getInputDrainCommands().size() > 1) {// at most the loop link and one data input link
			myMessenger.error("A DemandMerge can have at most TWO data inputs");
			return;
		} else
			super.addAnInputLink(L);
	}

	public void addAnOutputLink(Link L, String s) {
		if (getOutputFillCommands().size() > 0) {// at most the loop link and one data output link
			myMessenger.error("A DemandMerge can have at most ONE data output");
			return;
		} else
			super.addAnOutputLink(L);
	}

	public boolean checkMyTopology() {
		boolean ans = checkEnoughLinks(2, 1);
		ans = ans && super.checkMyTopology();
		return ans;
	}

	public void printMyStatistics() {
		super.printMyStatistics();
		String s = this.getFullName() + "use count = " + getUseCount();
		myMessenger.line(s);
	}

}// end of DemandMerge
