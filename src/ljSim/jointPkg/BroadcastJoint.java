/* Joint.java is the abstract Joint */
/* IES 26 April 1997 */
/* updated 2 June 2017 */
/* updated 6 June 2017 */
/* updated by Ivan 15 August 2017 */

/*The class BroadcastJoint has a list of input Links and a list of output Links.
 that hold the LinkCommands give it by the attached links.  
 */

package ljSim.jointPkg;

import ljSim.actionPkg.BroadcastAction;
import ljSim.components.Component;
import ljSim.components.Joint;

public class BroadcastJoint extends Joint {

	public static BroadcastJoint please(String name, Component parent) {
		return new BroadcastJoint(name, parent);
	}

	public String getTypeString() {
		return "BroadcastJoint";
	}

	// protected constructor accessible only from sub classes
	protected BroadcastJoint(String name, Component parent) {
		super(name, parent);
		addAnAction(new BroadcastAction("doAll", this));
		return;
	}// end of Joint constructor

}// end of class Joint
