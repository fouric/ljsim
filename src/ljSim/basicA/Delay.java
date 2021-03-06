//* Delay.java expresses delay values as integers */
/* IES  28 April 1997 */
/* updated 2 June 2017 */

/* The concept of Delay is distinct from the concept of time.  
 In this class a Delay is characterized only by an integer value.
 Delays can accumulate other delays or can take the max of another delay.
 Delay values may not be less than zero
 */
// updated 2 June 2017

package ljSim.basicA;

public class Delay {

	public static final Delay noDelay = new Delay(0, 0);
	private int length = 0;

	private Delay(int len) {
		length = len;
	}

	private Delay(int len, int sprd) {
		length = len;
	}

	// factory methods for creating delays: method is "of". Use Delay.of(etc)
	public static Delay of(int length) {
		return new Delay(length, 0);
	}

	public static Delay of(long length) {
		return new Delay((int) length, 0);
	}

	public static Delay of(int length, int spread) {
		return new Delay(length, spread);
	}

	// When needs to know duration to delay the When
	public int duration() {
		return length;
	} // this one is suspect, no one should need to know

	public boolean longerThan(Delay D) {
		return (length > D.length);
	}

	public boolean equals(Delay D) {
		return (length == D.length);
	}

	public boolean isZero() {
		return (length == 0);
	}

	public Delay maxDelay(Delay D) {
		int X = (length > D.length) ? length : D.length;
		return new Delay(X);
	}// end of maxDelay

	public Delay plus(int d) {
		return new Delay(d + this.length, 0);
	}// end of plus

	public Delay plus(Delay D) {
		return new Delay(D.length + this.length, 0);
	}// end of plus

	public String getString() {
		return String.valueOf(length);
	}

}// end of class Delay
