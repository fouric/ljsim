/* The RoundRobinJoinAction serves only the MergeJoint
* Intended to be the action of a MemoryLoopJoint */
/* written by IES 16 August 2017 */
/* adapted from earllier version 24 November 2017*/

package ljSim.actionPkg;

import ljSim.basicA.TimedValue;
import ljSim.basicA.Time;
import ljSim.components.Joint;

public class RoundRobinJoinAction extends Action {

	public RoundRobinJoinAction(String name, Joint parent) {
		super(name, parent);
	}

	private int index; // save the index for the grab action

	public void initialize() {
		return;
	}

	// can this RoundRobinJoinAction act now?
	public Time guard() {
		Time t = guard(0, 0); // time when loop is ready
		if (t == null)
			return null;
		// get the index value
		TimedValue tv = getInputValue(0);
		if (tv == null)
			return null;
		index = tv.getValue().getMyValue();// save the index for this command

		// now we need to guard the input and output data links
		Time tdata = guard(index, 1);
		if (tdata == null)
			return null;
		t = Time.lastOf(t, tdata);
		setMyGuardTime(t);
		return t;
	}

	// grabs the input and output links: Time t is usually the guard time
	public void grab(Time t)

	{// this joint has passed its guard, so all we have to do is update the index
		int loopDelay = 8;
		int spaceDelay = 5;
		int dataDelay = 7;

		Joint J = getMyJoint();
		Time actTime = this.guard();
		actTime = actTime.butAfter(t);

		// first let's get ready to move the data
		TimedValue oldData = getInCmd(index).getLinkOutputValue();
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
		getInCmd(index).grabIT(newSpace);
		getOutCmd(1).grabIT(newData);
	}// end of grab

	public boolean fire() {
		getOutCmd(0).doIT();
		getInCmd(0).enQueueMe(getMyGuardTime());
		getInCmd(index).enQueueMe(getMyGuardTime());
		getOutCmd(1).doIT();
		return true;
	} // end of fire

}// end of RoundRobinJoinAction
