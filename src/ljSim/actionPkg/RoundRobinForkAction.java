/* RoundRobinForkAction serves only the fork part.
 * Intended to be an action of a MemoryLoopJoint.
 * Written by Ivan on 24 November 2017
 */

package ljSim.actionPkg;

import ljSim.basicA.Time;
import ljSim.basicA.TimedValue;
import ljSim.components.Joint;

public class RoundRobinForkAction extends Action {

	public RoundRobinForkAction(String name, Joint parent) {
		super(name, parent);
	}

	public void initialize() {
		setCircmference();
	}

	private int index;// save the index for the grab action

	// can this RoundRobinFork act now?
	public Time guard() {
		Time ringTime = guard(0, 0); // time when loop is ready
		if (ringTime == null)
			return null;
		// get the index value
		TimedValue tv = getInputValue(0);
		if (tv == null)
			return null;
		index = tv.getValue().getMyValue();// save the index for this command

		// now we need to guard the input and output data links
		Time dataTime = guard(1, index);
		if (dataTime == null)
			return null;
		ringTime = Time.lastOf(ringTime, dataTime);
		setMyGuardTime(ringTime);
		return ringTime;
	}

	// grabs the input and output links: Time t is usually the guard time
	public void grab(Time t) {// this joint has passed its guard, so all we have to do is update the index
		int loopDelay = 8;
		int spaceDelay = 5;
		int dataDelay = 7;

		Joint J = getMyJoint();
		Time actTime = guard();
		actTime = actTime.butAfter(t);

		// first let's get ready to move the data
		TimedValue oldData = getInCmd(1).getLinkOutputValue();
		TimedValue newData = oldData.butAfter(actTime.delayedBy(dataDelay));
		TimedValue newSpace = oldData.drainedAndAfter(actTime.delayedBy(spaceDelay));

		// second compute the new index stuff
		TimedValue oldIndex = J.getInLink(0).getOutput();
		TimedValue freshIndex = oldIndex.nextRingValue(getCircumference());
		freshIndex = freshIndex.butAfter(actTime).delayedBy(loopDelay);
		TimedValue loopSpace = freshIndex.drained();

		// now do the values for the grabs
		getInCmd(0).grabIT(loopSpace);
		getOutCmd(0).grabIT(freshIndex);
		getInCmd(1).grabIT(newSpace);
		getOutCmd(index).grabIT(newData);
	}

	public boolean fire() {
		getOutCmd(0).doIT();
		getInCmd(0).enQueueMe(this.getMyGuardTime());
		getInCmd(1).enQueueMe(this.getMyGuardTime());
		getOutCmd(index).doIT();
		return true;
	}

}// end of RoundRobinForkAction
