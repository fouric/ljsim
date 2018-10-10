package ljSim.commands;

import ljSim.basicA.Time;
import ljSim.components.Component;

abstract public class UnsortedCommand extends Command {

	// The earlyQueue is unsorted.
	// static private Deque<UnsortedCommand> earlyQueue= new
	// LinkedList<UnsortedCommand>();
	// static protected Deque<UnsortedCommand>getEarlyQueue(){return earlyQueue;}
	// static protected void clearEarlyQueue(){earlyQueue= new
	// LinkedList<UnsortedCommand>();}
	// static public int getQueueSize() {return earlyQueue.size();}
	// reiterates this abstract method
	abstract public void doIT();

	// constructor
	protected UnsortedCommand(Component C) {
		super(C);
	}

	/*
	 * protected static Command getAtask() { Command task= earlyQueue.pollFirst();
	 * if(task == null)return null; commandsPending -- ; String s=
	 * "Removing from the earlyQueue " + task.getName(); s= s + " " +
	 * commandsPending + " tasks remain to do."; // myMessenger.line(s); return
	 * task; }
	 */
	// the queue for unsorted commands is called the "earlyQueue"
	// This needs embelishment to account for the sorted queue
	public boolean enQueueMe(Time t) {
		boolean ans = sq.enQueue(this);
		return ans;
	}

	@Override
	protected String getMyType() {
		return "UnsortedCommand";
	}

}
