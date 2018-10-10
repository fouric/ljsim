/* written by Ivan Sutherland on 5 January 2018 */
/* The SquareCueue class holds a two-dimensional queue called theQueue.
 * The first dimension uses a NavigableSet to sort SortedJointCommands by Time
 * In the second dimension the SquareQueue is unsorted:
 * Each SortedJointCommand has a Queue of UnsortedCommands called doBefore 
 * The first dimension sorts time into unsorted epochs of unsorted commands. 
 * This class contains the stuff to make that happen.
 * Use an instance of this class as a command queue.
 */

package ljSim.commands;

import java.util.Queue;
import java.util.NavigableSet;
import java.util.TreeSet;

import ljSim.commands.Command;
import ljSim.basicA.Messenger;

public class SquareQueue {

	private int commandsPending;
	private int maxCommandsPending;

	protected int getCommandsPending() {
		return commandsPending;
	}

	// I promise to put only SortedJointCommand into theQueue
	protected NavigableSet<Command> theQueue = new TreeSet<Command>();

	public int getQueueSize() {
		return theQueue.size();
	}

	protected static Messenger myMessenger = null;

	// the constructor
	public SquareQueue(Messenger m) {
		myMessenger = m;
		theQueue = new TreeSet<Command>();
		// insert last elements into theQueue
	}

	public boolean enQueue(UnsortedCommand c) {
		String s = "" + c.getTime().getMyTimeNum() + " Unsorted enQueue for " + c.getName();
		myMessenger.line(s);
		Command ac = theQueue.ceiling(c);
		if (ac == null)
			return false;
		// cast to SortedJointCommand
		SortedJointCommand sjc = (SortedJointCommand) ac;
		boolean ans = sjc.addToDeque(c);
		incrementCommandsPending();
		return ans;
	}

	public boolean enQueue(SortedJointCommand c) {
		String s = "" + c.getTime().getMyTimeNum() + " Sorted enQueue for " + c.getName();
		;
		myMessenger.line(s);
		Command cc = theQueue.ceiling(c);
		boolean ans = theQueue.add(c);
		if (ans == false)
			return false;
		if (cc != null) { // there is a ceiling, so cast it
			SortedJointCommand next = (SortedJointCommand) cc;
			Queue<UnsortedCommand> qq = next.takeMyQueue();
			if (qq != null)
				c.addToDeque(qq);
		}
		// no ceiling, so we're done
		incrementCommandsPending();
		return true;
	}

	// get the next command of any type or return null if none
	// get the next command of any type or return null if none
	public Command getNextCommand() {
		if (theQueue.size() == 0)
			return null;
		Command cc = theQueue.first();
		if (cc == null)
			return null; // theQueue is empty
		SortedJointCommand sjc = (SortedJointCommand) cc;
		UnsortedCommand uc = sjc.takeAsubTask();
		if (uc != null) { // found an unsorted subtask to do
			decrementCommandsPending();
			return uc;
		}
		// there is no unsorted command to do
		// so remove and return the sorted one
		theQueue.remove(sjc);
		decrementCommandsPending();
		return sjc;
	}

	protected int incrementCommandsPending() {
		commandsPending++;
		if (commandsPending > maxCommandsPending)
			maxCommandsPending = commandsPending;
		return commandsPending;
	}

	protected int decrementCommandsPending() {
		commandsPending--;
		return commandsPending;
	}

	public void printPendingTasks() {
		myMessenger
				.line("There are " + commandsPending + " CommandsPending " + " with maximum of " + maxCommandsPending);
		myMessenger.line("TheQueue.size() = " + theQueue.size());
		for (Command c : theQueue)
			c.printMe();
	}

	public void printStatistics() {
		// String s= "Command did " + commandsDone + " maxPending= " +
		// maxCommandsPending;
	}
	/*
	 * public static void testMe() { Messenger m= Messenger.please("test");
	 * SquareQueue x= new SquareQueue(m); Command[] ca; ca= new Command[10];
	 * Command[] cb; cb= new Command[10]; Joint j=
	 * OneInOneOutJoint.please("testJoint", null);
	 * 
	 * for(int i= 1; i<9 ; i++) { ca[i]= new SortedJointCommand (null);
	 * ca[i].setTime(Time.please(i*1000)); cb[i]= j.getOutCmd(0);
	 * cb[i].setTime(Time.please(i*400)); }
	 * 
	 * for(int i= 1; i<9 ; i++) { ca[i].enQueueMe(Time.please(i*1010)); }
	 * 
	 * for(int i= 1; i<9 ; i++) { cb[i].enQueueMe(Time.please(i*440)); } }//end of
	 * testMe
	 */

}// end of class SquareQueue
