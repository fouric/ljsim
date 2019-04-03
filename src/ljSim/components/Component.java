/* Component.java is the base type for all Components including Links and Joints */
/* IES 3 May 1997 */
/* Upadated IES 9 September 1997 */
/* Upadated IES 6 June 1997 */
/* Upadated IES19 December 2017 */

/*
 Component is the abstract base class for all components: Groups, Links and Joints.
 .
 Component keeps a list of all components.
 This is intended for debugging or for search for a particular component name,
 There is also a special entry called "theParent" that s the top of the Component tree.
 theParent is filled with the FIRST Component to be made with no parent.
 Other components made with no parent report an error.

 Link and Joint are a level of class below ComponentPart;
 each is a sub-class of Component, but knows only about the Commands of the other.
 */

///countLink

package ljSim.components;

import java.util.LinkedList;
import java.util.List;

import ljSim.basicA.Messenger;
import ljSim.basicA.Time;

public class Component {

	// Sub-classes must implement these methods
	public void masterClear() {
		return;
	}

	public String getTypeString() {
		return "Component - you should ONLY see this on the root object!";
	}

	static protected Messenger myMessenger = Messenger.createAppropriateMessenger("Component class", 2);
	private List<Component> children = new LinkedList<Component>();

	public List<Component> getChildren() {
		return children;
	}

	private String name = null;
	private Component parent = null;

	// ------------- ACCESSOR METHODS ---------------
	public Component getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public String getTypeAndName() {
		return name + ":" + getTypeString();
	}

	protected void setName(String n) {
		name = n;
	}

	protected Component getMyParent() {
		return parent;
	}

	// this is a service method to start a line out with a time.
	public String timeHerald(Time t) {
		String s;
		if (t == null) {
			s = "null-time";
		} else {
			s = Integer.toString(t.getMyTimeNum());
		}
		return s + " " + getName();
	}

	public Component(Component p, String n) {
		if (p == null) {
			// can't make a second Component without a parent
			myMessenger.error("can't make a Component without a parent");
			return;
		}
		parent = p;
		name = n;
		children.add(this);
		return;
	}

	// the Component name must be set after construction
	// ah, it looks like this was protected, and Component was an abstract class, because Link and Joint subclass component, and you're not supposed to actually instantiate a Component by itself. however, I'm going to do that, because we need roots. so there.
	public Component(Component p, String n, boolean isRoot) {
		if (p == null) {
			if (isRoot == false) {
				// can't make a second Component without a parent
				myMessenger.error("can't make a Component without a parent");
				return;
			}
		} else {
			p.getChildren().add(this);
		}
		//myMessenger.line("making the Component " + name + " of Type " + this.getTypeString());
		parent = p;
		name = n;
		return;
	}

	public String getFullName() {// recursive generation of full name
		if (parent == null || parent == this)
			return getTypeAndName();
		String p = parent.getName();
		String q = this.getTypeAndName();
		return (q + "." + p);
	}

	public void printTheComponents() {
		int num = children.size();
		myMessenger.line("");
		myMessenger.line("Printing all " + plural(num, " Component"));
		for (Component C : children) {
			Component p = C.getParent();
			if (p == null)
				myMessenger.line(C.getFullName() + " has no parent");
			else {
				String m = C.getFullName();
				myMessenger.say(m + " parent is ");
				myMessenger.line(p.getFullName());
			}
		}
		myMessenger.line("-----");
	}

	public void clearChildren() {
		int num = children.size();
		myMessenger.line("");
		myMessenger.line("Clearing all " + plural(num, " Component"));
		for (Component C : getChildren()) {
			C.masterClear();
		}
	}

	// this method appends "s" to string ss to form a plural
	static public String plural(int n, String ss) {
		if (n > 1)
			return n + ss + "s"; // make plural
		else if (n == 0)
			return "no" + ss; // is zero
		return n + ss;
	}

	public String toString() {
		//return getClass().getName() + "#<" + Integer.toHexString(hashCode()) + '>';
		return "#<COMPONENT :NAME \"" + name + "\" {" + Integer.toHexString(hashCode()).toUpperCase() + "}>";
	}
}
