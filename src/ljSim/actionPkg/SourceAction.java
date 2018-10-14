/* The SpirceActopm class checks if all outputs are EMPTY
 * and upon firing fills them */
/* Written by Ivan 13 August 2017 */
/* updated 18 November 2017 */

package ljSim.actionPkg;

import ljSim.basicA.Time;
import ljSim.basicA.TimedValue;
import ljSim.basicA.Value;
//import ljSim.chore.Chore;
import ljSim.commands.LinkCommand;
import ljSim.components.Joint;

public class SourceAction extends Action {

	private static int startTimeNumber = 100;
	private static int initialValue = 1000;

	private int interval;
	private int allowedFirings = 7;
	//private int sourceDelay = 6; // delay to fill source's outputs

	private TimedValue currentTValue = null;

	public void initialize() {
		return;
	}

	public SourceAction(String name, Joint parent, int period) {
		super(name, parent);
		// make the initial currentTValue
		currentTValue = new TimedValue(new Time(startTimeNumber), Value.of(initialValue), " from source");
		interval = period;
	}
	
	public SourceAction(String name, Joint parent) {
		super(name, parent);
		// make the initial currentTValue
		currentTValue = new TimedValue(new Time(startTimeNumber), Value.of(initialValue), " from source");
		interval = 50;
	}

	// advance the currentTime to the next clock tick and return data to send
	private void tick()// advance the currentTValue
	{
		// figure the next value to ship
		int x = currentTValue.getValue().getMyValue();
		Value v = Value.of(x + 200);
		TimedValue next = currentTValue.setValue(v);
		// now figure the next clock tick
		currentTValue = next.delayedBy(interval);// this is the next clock tick
		return;
	}

	// a Source is ready if all output Links are EMPTY
	public Time guard() {
		Joint J = getMyJoint();
		String s = getCombinedName();
		// ready is the time at which the last output became empty
		Time ready = guardOutputLinks();
		if (ready == null) {
			s = s + " has unready outputs ";
			myMessenger.line(s);
			return null;
		}
		if (J.getUseCount() > allowedFirings) {
			s = s + " has exceeded " + allowedFirings + " allowed firings";
			myMessenger.line(s);
			return null;
		}
		Time t = Time.lastOf(ready, currentTValue.getTime());
		s = J.timeHerald(t) + " Source Action guard OK";
		myMessenger.line(s);
		setMyGuardTime(t);
		return t;// this is earliest it could fire or null if it can't fire.
	}

	public void grab(Time t) {// produces a new output value
		Joint J = getMyJoint();
		TimedValue output = currentTValue.butAfter(t);
		for (LinkCommand C : J.getOutputFillCommands()) {
			C.grabIT(output);
		}
		return;
	}

	public boolean fire()// ready is when the I/O is ready
	{
		Joint J = getMyJoint();
		tick();// advance currentTValue
		Time t = this.getMyGuardTime();
		String s = J.timeHerald(t) + " firing with useCount ";
		String ss = J.getUseCount() + " of ";
		String sss = allowedFirings + " allowed";
		myMessenger.line(s + ss + sss);
		// so now we do the firing work
		for (LinkCommand C : J.getOutputFillCommands())
			C.enQueueMe(t);
		return true;
	}

}// end of class SourceAction
