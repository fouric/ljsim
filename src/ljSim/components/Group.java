/*
Class Comp is a concrete class intended to test class Component.
written by Ivan Sutherland 5 June 2017
Updated 6 June 2017
 

 * There is a unique entry called ROOT (TODO: make this not hardcoded) that is the root of the component tree.
 * Comp instances are kept in a List of Comp, 
 * This is intended as a model for LinkType and JointType
 */

package ljSim.components;

public class Group extends Component {

	private boolean recursionCheckBit = false; // to check for circular parentage

	// --------- Methods to override -----------
	public void masterClear() {
		System.out.println("clearing " + getName());
	}

	public String getTypeString() {
		return "Group";
	}

	public Group(String name) {
		super(null, name);
		recursionCheckBit = false;
		String check = checkForCircularParentage();
		if (check != null) {
			System.out.print(" Circular parantage found at ");
			System.out.println(check);
		}
		return;
	}

	public Group(String name, Component parent) {
		super(parent, name);
		this.setName(name);
		return;
	}

	// this method returns a String of the names of a circular parentage
	// It's very hard to make circular ancestry because you can reference only existing Components for ancestry
	protected String checkForCircularParentage() {
		String s = getName();
		if (s.equals("ROOT")) // ugh, this is hardcoded in
			return null; // we've got to the root
		if (getParent() == null) {
			System.out.println(getName() + " has null parent");
			return null;// everyone should have a parent
		}
		if (recursionCheckBit == true) { // I have a problem
			recursionCheckBit = false;
			return s;
		} else {
			// my recursion check bit is false, so check my parent
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
		Component root = new Component(null, "ROOT", true);
		Component A = new Group("A", root);
		String nn = A.getFullName();
		System.out.println("A's full name is: " + nn);
		Component B = new Group("B", root); // was ("B", getTheParent()) which would get the root node, but it's not obvious as to what the root node is...
		Component CC = new Group("CC", A);
		Component DDD = new Group("DDD", CC);
		Component BADone = new Group("BADone", null);
		Component BADtwo = new Group("BADtwo", CC);
		Component BADthree = new Group("BADthree", BADtwo);
		Component NN = new Group(null, CC);
		System.out.println("A is called " + A.getFullName());
		System.out.println("B is called " + B.getFullName());
		System.out.println("CC is called " + CC.getFullName());
		System.out.println("DDD is called " + DDD.getFullName());
		System.out.println("BADone is called " + BADone.getFullName());
		System.out.println("BADtwo is called " + BADtwo.getFullName());
		System.out.println("BADthree is called " + BADthree.getName());
		System.out.println("NN is called " + NN.getFullName());
		System.out.println("    trying recursive construction");

		root.clearChildren();
		root.printTheComponents();
		//Component RR = new Group("RR", BADtwo);
		// It's very hard to make circular ancestry because you can reference only existing Components for ancestry
	}

}
