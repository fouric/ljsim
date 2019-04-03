/* Messenger.java provides output message capability */
/* IES  25 April 1997 */
/* Updated IES  2 June 2017 */
/* 4 June 2017 comments added 
 * You must have a directory as named in outFilePlace
 */

/* class Messenger is an abstract class that defines how to print messages.
 Printing methods are:
 "say" writes to the output file, has no end of line
 "line" writes to the output file, terminates the line.
 "error" writes to System.err with a herald indicating where it's from.

 There are different kinds of Messengers, including:
 MessengerToSystem - gives output to System.out
 MessengerBrief - prints only a single character for each line, 80 to a line
 MessengerSilent - prints errors only
 MessengerDouble - output to two other Messengers
 MessengerToFile - gives output to its file
 if file= "test"  or "journal" it uses the same file for all calls.
 */

package ljSim.basicA;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public abstract class Messenger {
	// directory for output files needs to be put in line 396 or so
	protected static String outFilePlace = "/Users/ivans/outputJunk";
	protected String errorHerald = null;
	private static Messenger theBlankMessenger = null;
	protected String messengerType = "notYetSet";

	abstract public void say(String S);// without end of line

	abstract public void line(String S); // with end of line

	// the public has to use Messenger.please so we can check what kind to
	// provide.
	public static Messenger createAppropriateMessenger(String herald) {
		if (herald == null)
			return fixBlank();
		return new MessengerToSystem(herald);
	}// end of please just testing

	// levels produce: 0 = MessengerSilent, 1 = MessengerBrief, 2 = MessengerToSystem, 3 = MessengerDouble
	public static Messenger createAppropriateMessenger(String herald, int level) {
		if (herald == null)
			return fixBlank();
		if (level == 0)
			return new MessengerSilent(herald);
		if (level == 1)
			return new MessengerBrief(herald);
		if (level == 3)
			return new MessengerDouble(herald);
		return createAppropriateMessenger(herald);
	}// end of please

	// files produce: "journal" = theJournal, "test" = MessengerDouble
	public static Messenger please(String herald, String fileName) {
		if (herald == null)
			return fixBlank();
		if (fileName == null)
			return createAppropriateMessenger(herald);
		else
			return MessengerToFile.please(herald, fileName);
	}
	
	// the constructor is protected. Used only by sub classes
	public Messenger(String S) {
		// System.out.println("");
		// String t = this.messengerType;
		errorHerald = (S == null) ? "unknown source" : S;
	}

	private static Messenger fixBlank() {
		if (theBlankMessenger == null)
			theBlankMessenger = createAppropriateMessenger("unknown source", 2);
		return theBlankMessenger;
	}

	public void error(String S) {
		System.err.print("ERROR from " + errorHerald + ": ");
		System.err.println(S);
	}

	public static void testMe() {
		Messenger SS = Messenger.createAppropriateMessenger(null);
		System.out.println("111");
		testTheMessenger(SS);
		Messenger M1 = Messenger.createAppropriateMessenger("Messenger M1", 2);
		System.out.println("222");
		testTheMessenger(M1);
		Messenger M2 = Messenger.createAppropriateMessenger("Messenger M2", 1);
		System.out.println("333");
		for (int i = 20; i > 0; i--) {
			M2.line("count= " + i);
			// testTheMessenger(M2);
			for (int j = 100000; j > 0; j--)
				;// a delay
		} // end of for
		Messenger F1 = Messenger.please("Messenger F1", "journal");
		System.out.println("444");
		testTheMessenger(F1);
		Messenger F2 = Messenger.please("Messenger F2", "test");
		testTheMessenger(F2);
		System.out.println("555");
		Messenger F3 = Messenger.please("Messenger F3", "test");
		testTheMessenger(F3);
		System.out.println("666");
		return;
	}// end of testMe

	private static void testTheMessenger(Messenger M) {
		if (M == null) {
			System.out.println("testTheMessenger gets null");
			return;
		}
		M.say("from " + M.messengerType);
		M.say(" hello ");
		M.say(" hello again ");
		M.line(" end of line ");
		M.error(" just testing an error");
	}// end of testTheMessenger

}// end of Class Messenger

class MessengerToSystem extends Messenger {
	protected MessengerToSystem(String S) {
		super(S);
		messengerType = "MessengerToSystem";
		System.out.println("making messenger " + S + " of type " + messengerType);
	}

	public void say(String S) {
		System.out.print(S);
	}// without end of line

	public void line(String S) {
		System.out.println(S);
	}// with end of line
}// end of class MessengerToSystem

class MessengerBrief extends Messenger {
	private int count = 0;

	protected MessengerBrief(String S) {
		super(S);
		count = 0;
		messengerType = "MessengerBrief";
		System.out.println("making messenger " + S + " of type " + messengerType);
	}

	public void say(String S) {
		return;
	}// prints nothing

	// print only single characters
	public void line(String S) {
		if (count++ > 10) {
			count = 0;
			System.out.println("|");
		} else
			System.out.print("x");
	}// end of method line

}// end of class MessengerBrief

class MessengerSilent extends Messenger {
	protected MessengerSilent(String name) {
		super(name);
		messengerType = "MessengerSilent";
		System.out.println("making messenger " + name + " of type " + messengerType);
	}

//setting up file
	public void say(String S) {
		return;
	}// without end of line

	public void line(String S) {
		return;
	}// with end of line
}// end of class MessengerSilent

class MessengerDouble extends Messenger {
	private Messenger firstMessenger;
	private Messenger secondMessenger;

	protected MessengerDouble(String name) {
		super(name); // this provides the error Herrald
		messengerType = "MessengerDouble";
		firstMessenger = new MessengerToSystem(name);
		secondMessenger = new MessengerToFile(name, "test");
		System.out.println("making messenger " + name + " of type " + messengerType);
	}// end of MessengerDouble constructor

	public void say(String S) {
		firstMessenger.say(S);
		secondMessenger.say(S);
	}

	public void line(String S) {
		firstMessenger.line(S);
		secondMessenger.line(S);
	}

	public void error(String S) {
		firstMessenger.error(S);
		secondMessenger.line("ERROR from " + errorHerald + ": " + S);
	}// end of error

}// end of class MessengerDouble

class MessengerToFile extends Messenger {
	private static Messenger theJournal = null;
	private static OutputStream theOutputStream = null; // for "test"
	protected PrintStream myPrinter = null;

	public void say(String S) {
		myPrinter.print(S);
	}// without end of line

	public void line(String S) {
		myPrinter.println(S);
	}// with end of line

	// files produce: "journal" = theJournal, "test" = MessengerDouble
	static public Messenger please(String herald, String fileName) {
		// System.out.println("fileName is " + fileName);
		//boolean bb = "fileName".equals(fileName);
		if (fileName.equals("journal"))
			return fixJournal();
		if (fileName.equals("test"))
			return new MessengerDouble(herald);
		return new MessengerToFile(herald, fileName);
	}// end of please

	protected MessengerToFile(String name, String fileName) {
		super(name);
		messengerType = "MessengerToFile";
		myPrinter = getThePrinter(fileName);
		System.out.println("making messenger " + name + " of type " + messengerType);
	}// end of MessengerToFile constructor

	private static Messenger fixJournal() {
		if (theJournal == null)
			theJournal = new MessengerToFile("Journal", "journal");
		return theJournal;
	}// end of fixBlank

	// this returns the PrintStream to use. Call it with null to use System.out.
	// Call with "test" to use a special file.
	private static PrintStream getThePrinter(String name) {
		OutputStream OS = null;
		if (name == null)
			OS = System.out;
		else if (name.equals("test"))
			OS = fixTestOutputStream();
		else
			OS = makeFile(name);

		PrintStream PS = new PrintStream(OS, true);
		return PS;
	}// end of getThePrinter

	private static OutputStream fixTestOutputStream() {
		if (theOutputStream == null)
			theOutputStream = makeFile("test");
		return theOutputStream;
	}// end of fixTestPrinter

	private static OutputStream makeFile(String name) {
		System.out.println("setting up file called " + name);
		File F = new File(outFilePlace, name);
		OutputStream OS = null;
		try {
			OS = new FileOutputStream(F);
		} catch (IOException e) {
			System.err.println("file " + name + " error " + e.getMessage());
			return null;
		}
		System.out.println("done setting up output file " + name);
		return OS;
	}// end of makeFIle

}// end of class MessengerToFile
//fileName is
