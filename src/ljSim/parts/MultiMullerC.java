/* MultiMullerC.java holds the multi input Muller C element */
/* IES 1 May 1997 */
/* Updated IES 4 May 1997 */

/*
 * The MultiMullerC class does the multiple input task, formerly called
 * nodeguts. This is tricky code. The multi input muller C element has public
 * method Ding. It takes a Delay and a Value and return a Delay. If the return
 * Delay is non-null, it indicates the time of firing, if null, no firing. No
 * complaints
 */

/*
 * package ljSim.parts;
 * 
 * import ljSim.basicA.Messenger; import ljSim.basicA.Value; import
 * ljSim.basicA.Delay; import ljSim.basicA.When; /import
 * ljSim.chore.SimpleChore; import ljSim.interfaces.MullerInterface;
 * 
 * public class MultiMullerC { private static Messenger myMessenger =
 * Messenger.please("MultiMuller xlass", 2); private MullerInterface myUser =
 * null; private When bestWhen = null; private Value bestValue = null; private
 * int numHits = 0; private boolean busy = false; private int notBusyCount = 0;
 * private int busyCount = 0; private int useCount = 0;
 * 
 * // the constructor public MultiMullerC(Messenger M, MullerInterface U) { if
 * (M != null) myMessenger = M; myUser = U; if (myUser == null)
 * System.err.println("MultiMuller must have a user"); masterClear(Delay.noDelay,
 * null); return; }// end of MullerC constructor
 * 
 * public void masterClear(Delay d, Value v) { bestWhen = null; bestValue =
 * null; numHits = 0; busy = false; notBusyCount = 0; busyCount = 0; useCount =
 * 0; }// end of masterClear
 * 
 * public void ding(Delay d, Value v) { if (++numHits >
 * myUser.getNumberInputs()) { System.err.println("more hits than inputs");
 * return; } When W = SimpleChore.getWhen(d); if (bestWhen == null) { bestWhen =
 * SimpleChore.getWhen(d); bestValue = v; }// end of if else { When X =
 * SimpleChore.getWhen(d); bestWhen = When.laterOf(X, bestWhen); // later of
 * both start // and end // bestValue= bestValue.combineWith(v); }// end of else
 * // do the ding check if (!busy) { // start of the protected section
 * notBusyCount++; busy = true; // if(myUser.getNumberInputs() > 1) //
 * System.out.println("MCC from " + myUser.getName() + // " with inputs=" +
 * myUser.getNumberInputs() + " hits= " + // numHits); while (numHits ==
 * myUser.getNumberInputs()) { // all have fired //
 * System.out.println(" all have fired"); Value V = bestValue; Delay dd =
 * SimpleChore.getDelay(bestWhen); numHits = 0; bestValue = null; bestWhen =
 * null; useCount++; myUser.mullerOutput(dd, V); }// end of while busy = false;
 * }// end of if busy, end of protected section else {
 * System.out.println("MultiMullerC is busy in " + myUser.getName()); busyCount++;
 * }// end of else return; }// end of dingA
 * 
 * public void printStatistics() {
 * System.out.println("Statistics for MultiMullerC at " + myUser.getName() +
 * " numInputs= " + myUser.getNumberInputs() + ": notBusyCount= " + notBusyCount
 * + " busyCount= " + busyCount + " useCount= " + useCount); }// end of
 * printStatistics
 * 
 * }// end of class MullerC
 */
