/*
Class Comp is a concrete class intended to test class Component.
written by Ivan Sutherland 5 June 2017
Updated 6 June 2017
 

 * There is a unique entry called TOP that is the root of the component tree.
 * Comp instabces are kept in a List of Comp, 
 * This is intended as a model for LinkType and JointType
 */

package ljSim.components;

import java.util.List;

public class Group extends Component {

	private boolean recursionCheckBit = false; // to check for circular parentage

	// --------- Methods to override -----------
	public void masterClear() {
		myMessenger.line("clearing " + getName());
	}

	public String getTypeString() {
		return "Group";
	}

	// the constructors
	public Group(String name) {
		super(null);
		List<Component> x = getComponents();
		x.add(this);
		constructorGuts(name);
		recursionCheckBit = false;
		String check = checkForCircularParentage();
		if (check != null) {
			myMessenger.say(" Circular parantage found at ");
			myMessenger.line(check);
		} // end of if
		return;
	}// end of constructor

	public Group(String name, Component parent) {
		super(parent);
		this.setName(name);
		return;
	}// end of constructor

	protected void constructorGuts(String name) {
		return;
	}// end of constructorGuts

	// this method returns a String of the names of a circular parentage
	// It's very hard to make circular ancestry because you can
	// reference only existing Components for ancestry
	protected String checkForCircularParentage() {
		String s = getName();
		if (s.equals("TOP"))
			return null; // we've got to the root
		if (getParent() == null) {
			myMessenger.line(getName() + " has null parent");
			return null;// everyone should have a parent
		}
		if (recursionCheckBit == true) { // I have a problem
			recursionCheckBit = false;
			return s;
		} else // my recursion check bit is false, so check my parent
		{
			recursionCheckBit = true; // leave a trail
			Group x = (Group) getParent();
			String p = x.checkForCircularParentage();
			recursionCheckBit = false; // remove the trail
			// if my parent check passed p is null.
			if (p == null)
				return null; // my parent is OK
			// my parent is NOT OK
			return s;
		}
	}// end of circularParentage

	public static void testMe() {
		Component A = new Group("A", null);
		String nn = A.getFullName();
		myMessenger.line("A's full name is: " + nn);
		Component B = new Group("B", getTheParent());
		Component CC = new Group("CC", A);
		Component DDD = new Group("DDD", CC);
		Component BADone = new Group("BADone", null);
		Component BADtwo = new Group("BADtwo", CC);
		Component BADthree = new Group("BADthree", BADtwo);
		Component NN = new Group(null, CC);
		myMessenger.line("A is called " + A.getFullName());
		myMessenger.line("B is called " + B.getFullName());
		myMessenger.line("CC is called " + CC.getFullName());
		myMessenger.line("DDD is called " + DDD.getFullName());
		myMessenger.line("BADone is called " + BADone.getFullName());
		myMessenger.line("BADtwo is called " + BADtwo.getFullName());
		myMessenger.line("BADthree is called " + BADthree.getName());
		myMessenger.line("NN is called " + NN.getFullName());
		myMessenger.line("    trying recursive construction");

		Component.clearAllComponents();
		Component.printTheComponents();
		Component RR = new Group("RR", BADtwo);
		// It's very hard to make circular ancestry because you can
		// reference only existing Components for ancestry u
	}// end of testMe

}// end of class ComponentTest