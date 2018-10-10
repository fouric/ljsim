/* A Joint issues JointCommands to connect it to Links.
* It has only one type to wake it up.
* When asked for their target they return type Joint
*/
/* Written by Ivan 25 August 2017 */

package ljSim.commands;

import ljSim.basicA.Time;
import ljSim.components.Joint;
import ljSim.components.Link;

abstract public class JointCommand extends UnsortedCommand {

	// String myLinkName= null;
	private Link mySource = null;

	public void setSource(Link x) {
		mySource = x;
	}

	public Link getSource() {
		return mySource;
	}

	public Joint getTarget() {
		return (Joint) super.getTarget();
	}

	// Constructor
	protected JointCommand(Joint J) {
		super(J);
	}

	public boolean enQueueMe(Time t) {
		return super.enQueueMe(t);
	}

	@Override
	public void doIT() {
		Joint J = getTarget();
		J.wakeAndDo(mySource);
		return;
	}

	@Override
	protected String getMyType() {
		return "JointCommand";
	}

}// end of JointCommand class
