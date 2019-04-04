/* RingCopy action copies from an input Link to an output Link
 * advancing the value mod circumference.
 *  the value 
 * 
 * 
 */
/* Written by Ivan 22 December 2017 */

package ljSim.actionPkg;

import ljSim.basicA.Time;
import ljSim.basicA.TimedValue;
import ljSim.basicA.Value;
import ljSim.components.Joint;

public class RingCopyAction extends CopyAction {
	
	public RingCopyAction(String name, Joint parent, int from, int to) {
		super(name, parent, from, to);
	}// the ring links are always index 0

	public void initialize() {
		System.out.println("initializing RingCopyAction");
		super.initialize();
		this.setCircmference();
	}

	public Value getRingIndexValue() {
		return this.getInputValue(0).getValue();
	}

	// This is the special grab action, using myValue as the data
	public void grab(Time t) // t is when to act, usually getMyGuardTime()
	{
		// used only after guard passes
		// myValue (obtained during the guard process) holds the data to move
		TimedValue tv = getMyValue().butAfter(t);
		Value oldVal = getRingIndexValue();
		Value newVal = oldVal.nextRingValue(getCircumference());
		tv = tv.replaceValue(newVal);
		grabOut(outCom, tv, fillDelay);
		grabIn(inCom, tv, drainDelay);
		return;
	}

}
