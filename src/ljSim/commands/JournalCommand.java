/* class JournalCommand puts a comment out on the journal file */
/* IES 25 April 1997 */

package ljSim.commands;

import ljSim.basicA.Time;

public class JournalCommand extends Command {

	//private String myComment = "is Empty";
	//private static Messenger myJournal = null;

	// protected Time guarddIT(){return Time.please(0);} //returns the time it can
	// be done
	// protected Time retrydIT(){return Time.please(1);} //returns a suitable retry
	// time

	public static void makeComment(String S) {
		JournalCommand J = new JournalCommand(S);
		J.doIT();
		return;
	}// end of makeComment

	public JournalCommand(String S) {
		super(null);
		/*
		if (S != null) {
			myComment = S;
		}*/
		//myJournal = Messenger.please("JournalCommand class", "journal");
	}// end of makeComment

	protected String getMyType() {
		return "JournalCommand";
	}

	// this is the flashback part
	public void doIT() {
		// if(d.isZero())
		// myJournal.line("JournalSays: " + Chore.getTimeString(d) + " "
		// + myComment + " " + v.getString());
		// else queueIT(d, v);
		return;
	}// end of doIT

	public boolean enQueueMe(Time t) {
		return true;
	}

	public static void testMe() {
		myMessenger.line("starting testJournalCommand");
		makeComment("two");
		makeComment("one");
		makeComment("four");
		makeComment("three");
		makeComment("five");
		Command.doAllTasks();
		myMessenger.line("ending testJournalCommand");
	}// end of testJournalCommand
}// end of class JournalCommand
