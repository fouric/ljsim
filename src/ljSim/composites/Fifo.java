/* Fifo is a class that generates FIFOs between two existing Links.
 * Fifo's constructor thakes its input and output Link as arguments.
 * 
 */

package ljSim.composites;

import ljSim.components.Component;
import ljSim.components.Joint;
import ljSim.components.Link;

public class Fifo {

	private static int fifoNumber = 1;

	// Constructors
	protected Fifo(String name, Link in, Link out, int length) {
		// First we'll make a FIFO from all new parts
		//String s = "FIFO-" + fifoNumber + "-" + name;
		//Group par = new Group(s, parent);

		if (length < 1) {
			System.err.println("can't construct a FIFO with length " + length);
			// return 0;
		}
		String f = in.getFullName();
		String t = out.getFullName();
		System.out.println("made FIFO" + fifoNumber + " with " + Component.plural(length, " Link") + " from " + f + " to " + t);
		fifoNumber++;

	}

	protected Fifo(Joint in, Joint out, int length) {

	}

}// end of class Fifo
