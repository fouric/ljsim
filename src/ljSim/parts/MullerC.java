/* MullerC.java holds the two input Muller C element */
/* IES 1 May 1997 */
/* Updated IES 4 May 1997 */

/*
 * The MullerC responds only after both its inputs have been dinged. The two
 * input muller C element has public methods dingA and dingB. They take a Delay
 * and a Value and return a Delay, ignoring the Value. If the return Delay is
 * non-null, it indicates the time of firing, if null, no firing. These methods
 * complain if they get a double ding. This class has no delays of its own. You
 * must include its delays wherever it is used.
 */

/*
 * package ljSim.parts;
 * 
 * import ljSim.basicA.Messenger; import ljSim.basicA.Value; import
 * ljSim.basicA.Delay; import ljSim.basicA.When; import ljSim.chore.SimpleChore;
 * 
 * public class MullerC { private static Messenger myMessenger =
 * Messenger.please("MullerC class", 2); private int useCount = 0; private When
 * firstWhen = null; // Time of first arrival private Value firstValue = null;
 * private boolean hasDingedA = false; private boolean hasDingedB = false;
 * private String myName = "nameless MullerC";
 * 
 * // the constructor public MullerC(Messenger M, String S) { if (M != null)
 * myMessenger = M; if (S != null) myName = S; firstWhen = null;
 * masterClear(Delay.noDelay, null); return; }// end of MullerC constructor
 * 
 * public void masterClear(Delay d, Value v) { useCount = 0; firstWhen = null;
 * firstValue = null; hasDingedA = hasDingedB = false; }// end of masterClear
 * 
 * public Delay dingA(Delay d, Value v) { if (hasDingedA)
 * System.err.println("MullerC " + myName + " double ding A"); hasDingedA = true;
 * return doDing(d, v); }// end of dingA
 * 
 * public Delay dingB(Delay d, Value v) { if (hasDingedB)
 * System.err.println("MullerC " + myName + " double ding B"); hasDingedB = true;
 * return doDing(d, v); }// end of dingB
 * 
 * private Delay doDing(Delay d, Value v) { if (firstWhen == null) { firstWhen =
 * SimpleChore.getWhen(d); firstValue = v; return null; }// end of if else { //
 * fire it; useCount++; hasDingedA = false; hasDingedB = false; When X =
 * SimpleChore.getWhen(d); When w = When.laterOf(X, firstWhen); // later of both
 * start // and end firstWhen = null; firstValue = null; return
 * SimpleChore.getDelay(w); }// end of else }// end of dingA
 * 
 * public void printMyStatistics() {
 * System.out.println("Statistics for MullerC: hasDingedA= " + hasDingedA +
 * " hasDingedB= " + hasDingedB + " useCount= " + useCount); }// end of
 * printStatistics
 * 
 * }// end of class MullerC
 */
