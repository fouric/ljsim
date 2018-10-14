
/* Joint.java is the abstract Joint */
/* IES 26 April 1997 */
/* updated 2 June 2017 */
/* updated 6 June 2017 */
/* updated by Ivan 15 August 2017 */

/*The class BroadcastJoint has a list of input Links and a list of output Links.
 that hold the LinkCommands give it by the attached links.  
 */

package ljSim.jointPkg;

import ljSim.actionPkg.CopyAction;
import ljSim.components.Component;
import ljSim.components.Joint;
import ljSim.components.Link;

public class OneInOneOutJoint extends Joint {

	public String getTypeString() {
		return "OneInOneOutJoint";
	}

	public OneInOneOutJoint(String name, Component parent) {
		super(name, parent);
		addAnAction(new CopyAction("copy", this, 0, 0));
		return;
	}

	// to help debugging: a breakpoint here will stop when this type of joint wakes
	public boolean wakeAndDo(Link who) {
		return super.wakeAndDo(who);
	}

	// ---------- topology builders ----------------------

	public void addAnInputLink(Link lk) {
		super.addAnInputLink(lk, 1);
	}

	public void addAnOutputLink(Link lk) {
		super.addAnOutputLink(lk, 1);
	}

	// ---------- topology check ----------------------
	public boolean checkMyTopology() {
		boolean ans = super.checkMyTopology();
		ans = ans && checkEnoughLinks(1, 1);
		return ans;
	}

}// end of class OneInOneOutJoint
