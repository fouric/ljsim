/* the Time class records a simulandum time as an integer.
 * the TValue class will have both a time and a Value.
 */
/* Written by Ivan Sutherland on 3 September 2017 */
/* Upgraded to have Time arrays by Ivan Sutherland on9 January 2018 */

/* Class Time replaces class When which is deprecated.
 * The major difference between them is that When had a beginning and end
 * but Time is just a point in time.
 * 
 */

package ljSim.basicA;

import java.util.List;

public class Time {
	private int myTimeNum = 0;

	public int getMyTimeNum() {
		return myTimeNum;
	}

	// the latest possible time is called farFuture
	static private int farFuture = Integer.MAX_VALUE;

	static public Time getFarFuture() {
		return new Time(farFuture);
	}

	public static Time theLastTime = new Time(Integer.MAX_VALUE);
	public static Time zeroTime = new Time(0);

	public Time(int t) {
		myTimeNum = Math.abs(t);
	}

	public boolean isBefore(Time x) {
		if (x == null) {
			return false;
		} else if (myTimeNum >= x.myTimeNum) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isAfter(Time x) {
		if (x == null) {
			return false;
		} else if (myTimeNum <= x.myTimeNum) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isSameAs(Time x) {
		if (x == null) {
			return false;
		} else if (myTimeNum != x.myTimeNum) {
			return false;
		} else {
			return true;	
		}
	}

	// get a time later than this one
	public Time delayedBy(int d) {
		// what if d is negative?
		return new Time(myTimeNum + d);
	}

	// get this time unless Time t exists and is later
	public Time butAfter(Time t) {
		if (t == null)
			return this;
		if (this.isAfter(t))
			return this;
		else
			return t;
	}

	public Time butAfter(TimedValue v) {
		if (v == null)
			return this;
		Time tv = v.getTime();
		if (this.isAfter(tv))
			return this;
		return tv;
	}

	// return the later of two times, or the only one that exists
	// or null if neither exist
	public static Time earliestOf(Time p, Time q) {
		if (p == null)
			return q;
		if (q == null)
			return p;
		if (p.isBefore(q))
			return p;
		else
			return q;
	}

	// return the index of the latest non-null entry in an array of times.
	// or -1 all are null
	public static int earliestIndex(List<Time> aa) {
		int bestActionIndex = -1;
		Time earliestTime = null;
		int n = aa.size();
		for (int i = 0; i < n; i++) {// iterate through the actionTimes
			Time actionTime = aa.get(i); // null if impossible
			if (actionTime == null)
				break; // this action can't be done
			if (earliestTime == null || actionTime.isBefore(earliestTime)) {
				earliestTime = actionTime;
				bestActionIndex = i;
			}
		} // end of iteration
		return bestActionIndex;// -1 if input has no times
	}

	// return the later of two times, or the only one that exists
	// or null if neither exist
	public static Time latestOf(Time p, Time q) {
		if (p == null)
			return q;
		if (q == null)
			return p;
		if (p.isAfter(q))
			return p;
		else
			return q;
	}

	// return the later of two times: BOTH MUST EXIST
	// return null if either is null.
	public static Time lastOf(Time p, Time q) {
		if (p == null || q == null)
			return null;
		Time ans = p.butAfter(q);
		return ans;
	}

	// return the earlier of the times of two TValues if both exist
	// if only one exists, return the time of the other
	// if neither exist return null
	public static Time earlierOf(TimedValue p, TimedValue q) {
		if (p == null && q == null)
			return null;
		if (p == null)
			return q.getTime();
		if (q == null)
			return p.getTime();
		Time tp = p.getTime();
		Time tq = q.getTime();
		if (tp.isBefore(tq))
			return tp;
		else
			return tq;
	}

	public static Time earlierOf(Time p, TimedValue q) {
		if (p == null || q == null)
			return null;
		Time tq = q.getTime();
		if (p.isBefore(tq))
			return p;
		else
			return tq;
	}

	public static int between(Time a, Time b) {
		if (a == null || b == null)
			return 0;
		int ta = a.myTimeNum;
		int tb = b.myTimeNum;
		int diff = ta - tb;
		int ans = diff < 0 ? -diff : diff;
		return ans;
	}

	public String atTimeString() {
		String ans = " at Time " + myTimeNum;
		return ans;
	}

}// end of class Time
