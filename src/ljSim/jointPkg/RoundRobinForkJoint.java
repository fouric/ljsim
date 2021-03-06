/* a RoundRobinFork distributes input values to its outputs in sequence
 * see also RoundRobinJoin 
 */
/* Written by Ivan on 16 August 2017 */

package ljSim.jointPkg;

import ljSim.actionPkg.Action;
import ljSim.actionPkg.RoundRobinForkAction;
import ljSim.components.Component;
import ljSim.components.Joint;
import ljSim.components.Link;

public class RoundRobinForkJoint extends Joint {

	public RoundRobinForkJoint(String name, Component parent) {
		super(name, parent);
		addAnAction(new RoundRobinForkAction("RoundRobinFork", this));
		JointFactory.makeAloop(this);
		return;
	}

	public void masterClear() {
		super.masterClear();
		for (Action A : actions) {
			A.initialize();
		}
	}

	// to help debugging: a breakpoint here will stop when this type of joint wakes
	public boolean wakeAndDo(Link why) {
		return super.wakeAndDo(why);
	}

	public String getTypeString() {
		return "RoundRobinFork";
	}

	// ---------- topology builders ----------------------
	public void addAnInputLink(Link L, String s) {
		super.addAnInputLink(L, 2);
	}

	public boolean checkMyTopology() {
		return checkEnoughLinks(2, 2) && super.checkMyTopology();
	}

}
