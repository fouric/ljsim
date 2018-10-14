/* a CopyAction copies data from an input link to an output link 
 * Written by Ivan 15 November 2017
 * Each copy action has a fromIndex and a toIndex
 * Set when the action is created
 * Its guard tests if the Links at those indices are FULL and EMPTY
 * Its fire method moves data from input to output 
 * Two of these are used in the DemandMerge Joint
 */

package ljSim.actionPkg;

import ljSim.basicA.Time;
import ljSim.basicA.TimedValue;
import ljSim.commands.LinkCommand;
import ljSim.components.Joint;

public class CopyAction extends Action {
	// the index of the input for this action
	private int inputIndex; // The index to my input in the Joint's table
	private int outputIndex;// The index to my output in the Joint's table
	private int useCount; // The number of times I moved data

	public int getUseCount() {
		return useCount;
	}

	protected LinkCommand inCom = null; // my input LinkCommand (from inputIndex)
	protected LinkCommand outCom = null;// my output LinkCommand (from outputIndex)

	// delay constants
	protected int drainDelay = 4;
	protected int fillDelay = 4;

	public void setDrainDelay(int d) {
		drainDelay = d;
	}

	public void setFillDelay(int d) {
		fillDelay = d;
	}

	public CopyAction(String name, Joint parent, int from, int to) {
		super(name, parent);
		inputIndex = from;
		outputIndex = to;
	}

	public void initialize() {
		// check to see that it's connected
		inCom = getMyJoint().getInCmd(inputIndex);
		outCom = getMyJoint().getOutCmd(outputIndex);
	}

	// the guard checks the time of Link at inputIndex and outputIndex
	// they have to be full and empty respectively
	// returns the time of the later arrival or null if either is missing
	public Time guard() {
		Time ans = guard(inCom, outCom);
		if (ans == null)
			return ans;
		setMyGuardTime(ans); // this is the "fire" time
		setMyValue(inCom.getLinkOutputValue());// this is the value to move
		return ans;
	}// end of guard

	// This is the default grab action, using myValue as the data
	public void grab(Time t) // t is when to act, usually getMyGuardTime()
	{
		// used only after guard passes
		// myValue (obtained during the guard process) holds the data to move
		TimedValue tv = getMyValue().butAfter(t);
		grabOut(outCom, tv, fillDelay);
		grabIn(inCom, tv, drainDelay);
		return;
	}

	public boolean fire() {
		// move the data from input to output
		inCom.enQueueMe(getMyGuardTime());
		outCom.doIT();
		useCount++;
		return true;
	}

	public String copyActionString() {
		String ans = (getCombinedName() + " from " + inputIndex + " to " + outputIndex);
		return ans;
	}

}// end of CopyAction
