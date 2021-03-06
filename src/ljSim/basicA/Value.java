/* Value.java holds a value to pass through a Command */
/* IES  25 April 1997 */
/* Updated IES  2 June 2017 */

/* class Value is a value for a command to use */
/* the concept of a Value is encapsulated here before I know very much about
 what value should do.  
 I have included some possible tests on values, but these are not yet used
 */

package ljSim.basicA;

public class Value {
	public static Value zeroValue = new Value(0); // used to clear out registers
	public static Value noValue = new NullValue();
	private int myValue = 0;
	static private int counter = 0;
	private String mySource = null;

	public int getMyValue() {
		return myValue;
	}

	public String getMySource() {
		return mySource;
	}

	// factory method "of" is used to create new values. use
	// Value.of(arameters);
	public static Value of(int n) {
		return new Value(n);
	}

	public static Value of(int n, String s) {
		return new Value(n, s);
	}

	public void setString(String s) {
		mySource = s;
	}

	// count by one from zero up to and including argument
	public Value next(int i) {
		return (myValue >= i) ? zeroValue : new Value(myValue + 1);
	}// end of next

	// alternate between the argument and zero
	public Value alternate(int i) {
		return (testBit(i)) ? zeroValue : new Value(i);
	}// end of alternate

	// the constructors
	protected Value(int x) {
		myValue = x;
	}

	protected Value(int x, String source) {
		myValue = x;
		mySource = source;
	}

	public boolean testBit(int bit) {
		return ((myValue & bit) != 0);
	}

	public boolean equals(int x) {
		return (myValue == x);
	}

	public String getString() {
		String src = (mySource == null) ? "" : mySource;
		return "Value= " + myValue + " " + src;
	}

	static public Value nextValue(String s) {
		counter++;
		return of(counter, s);
	}

	// this method steps a Value forward around a ring
	public Value nextRingValue(int modulus) {
		int oldCount = getMyValue();
		int newCount = oldCount + 1;
		if (newCount > modulus)
			newCount = 1;
		String ss = getMySource();
		Value ans = Value.of(newCount, ss);
		return ans;
	}

}// end of Class Value

class NullValue extends Value {

	protected NullValue() {
		super(0);
	}

	public String getString() {
		return "Value= none";
	}

}// end of class NullValue
