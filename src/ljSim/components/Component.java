/* Component.java is the base type for all Components including Links and Joints *//* IES 3 May 1997 *//* Upadated IES 9 September 1997 *//* Upadated IES 6 June 1997 *//* Upadated IES19 December 2017 *//*  Component is the abstract base class for all components: Groups, Links and Joints. . Component keeps a list of all components.  This is intended for debugging or for search for a particular component name,  There is also a special entry called "theParent" that s the top of the Component tree. theParent is filled with the FIRST Component to be made with no parent. Other components made with no parent report an error. Link and Joint are a level of class below ComponentPart; each is a sub-class of Component, but knows only about the Commands of the other. *////countLink package ljSim.components;import java.util.LinkedList;import java.util.List;import ljSim.basicA.Messenger;import ljSim.basicA.Time;public abstract class Component {	// Sub-classes must implement these methods	abstract public void masterClear();	abstract public String getTypeString();	static protected Messenger myMessenger = Messenger.createAppropriateMessenger("Component class", 2);	static private List<Component> theComponents = new LinkedList<Component>();	static public List<Component> getComponents() {		return theComponents;	}	static private Component theParent = null;	static public Component getTheParent() {		return theParent;	}	static protected boolean isTheParentNull() {		return theParent == null;	}	private String myName = null;	private Component myParent = null;	// ------------- ACCESSOR METHODS ---------------	public Component getParent() {		return myParent;	}	public String getName() {		return myName;	}	public String getTypeAndName() {		return myName + ":" + getTypeString();	}	protected void setName(String n) {		myName = n;	}	protected Component getMyParent() {		return myParent;	}	// this is a service method to start a line out with a time.	public String timeHerald(Time t) {		String s;		if (t == null)			s = "null-time";		else			s = Integer.toString(t.getMyTimeNum());		s = s + " " + getName();		return s;	}	// the Component name must be set after construction	protected Component(Component parent) {		if (parent == null && theParent != null) {			// can't make a second Commponent without at parent			myMessenger.error("can't make a Component without a parent");			return;		}		String tp = this.getTypeString();		// myMessenger.line("making the Component " + myName + " of Type " + t);		if (theParent == null) { // theParent has NOT YET been defined, so this one is theParent			theParent = this;			theParent.setName("TOP");			myMessenger.line(timeHerald(null) + " is now theParent");		} else { // this is the ONLY way to add to theComponents list			myParent = parent;			theComponents.add(this);		} // end of else		return;	}// end of constructor	public String getFullName() {// recursive generation of full name		if (myParent == null || myParent == this)			return getTypeAndName();		String p = myParent.getName();		String q = this.getTypeAndName();		return (q + "." + p);	}// end of getFullName	static public void printTheComponents() {		int num = theComponents.size();		myMessenger.line("");		myMessenger.line("Printing all " + plural(num, " Component"));		for (Component C : theComponents) {			Component p = C.getParent();			if (p == null)				myMessenger.line(C.getFullName() + " has no parent");			else {				String m = C.getFullName();				myMessenger.say(m + " parent is ");				myMessenger.line(p.getFullName());			}		} // end of for		myMessenger.line("-----");	}// end of printTheComponents	static public void clearAllComponents() {		int num = theComponents.size();		myMessenger.line("");		myMessenger.line("Clearing all " + plural(num, " Component"));		for (Component C : getComponents()) {			C.masterClear();		} // end of for loop	}// end of clearAllComponents	// this method appends "s" to string ss to form a plural	static public String plural(int n, String ss) {		if (n > 1)			return n + ss + "s"; // make plural		else if (n == 0)			return "no" + ss; // is zero		return n + ss;	}}// end of class Component///Components