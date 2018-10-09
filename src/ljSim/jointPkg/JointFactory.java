/*The Joint factory has methodes to build things like FIFOs
 * 
 */
 /* Written by Ivan on 16 August 2017 */


package ljSim.jointPkg;


import ljSim.basicA.Messenger;
import ljSim.basicA.Time;
import ljSim.basicA.TimedValue;
import ljSim.basicA.Value;
import ljSim.components.Component;
import ljSim.components.Group;
import ljSim.components.Joint;
import ljSim.components.Link;

 public class JointFactory {

     private static Messenger myMessenger = Messenger.please("Factory", 2);   
     private static int fifoNumber = 1;

     /* CONSTRUCTOR IF NEEDED
     public JointFactory(Messenger M) {
         myMessenger = (M != null) ? M : Messenger.please("Factory", 2);
         return;
     }// end of PathFactory constructor
*/

    //connect Joint A to Joint B with a FIFO with "length" Links 
    //return the number of links made
    static public int makeFIFO(Component parent, Joint A, Joint B, int length)
        {
            String s = "FIFO-" + fifoNumber;
            Group par = new Group(s, parent);

            int jointNums = 1;
            int linkNums = 1;

            if (length < 1)
                {
                    myMessenger.error("can't make a FIFO of size " + length);
                    return 0;
                }
            String f = A.getFullName();
            String t = B.getFullName();
            String ss = "made FIFO" + fifoNumber + " with "
                    + Component.plural(length, " Link") + " from " + f + " to "
                    + t;
            myMessenger.line(ss);
            fifoNumber++;
            Joint from = A;
            Joint to = null;
            int k = 1;
            while (k <= length)
                {
                    if (k < length)
                    // make another joint
                        {
                            String jn = String.valueOf(jointNums);
                         // will be assigned unique number
                            to = OneInOneOutJoint.please(null, par);
                        } else
                        to = B;
                    Link lk = Link.please(null, par);// unique name will be
                                                     // assigned
                    connect(from, to, lk);
                    from = to;
                    k++;
                    ;
                }// end of while
            return length;
        }// end of makeFIFO  

    // Connect Joint from to Joint to using link via
    static public void connect(Joint from, Joint to, Link via)
        {
            connect(from, via);
            connect(via, to);
            return;
        }// end of connect two joints

    // connect a from a Joint to a Link
    static public void connect(Joint from, Link to)
        {
            from.addAnOutputLink(to);
            return;
        }// end of connect

    // connect from a Link to a Joint
    static public void connect(Link from, Joint to)
        {
            to.addAnInputLink(from);
            return;
        }// end of connect

    // connect a RR fork f to a RR join j with w arms of l links each
    // return the total number of links
    static public int makeRR(Component parent, RoundRobinForkJoint f,
            RoundRobinJoinJoint j, int w, int l)
        {
            int numLinks = 0;
            for (int i = 0; i < w; i++)
                {
                    int n = makeFIFO(parent, f, j, l);
                    numLinks = numLinks + n;
                }
            myMessenger.line("makeRR made "
                    + Component.plural(numLinks, " Link"));
            return numLinks;
        }// end of makeRR

    // connect a RR fork f to a RR join j with w arms of l links each
    // return the total number of links
    static public int makeRR(Component parent, RoundRobinJoint f,
            RoundRobinJoint j, int w, int l)
        {
            int numLinks = 0;
            for (int i = 0; i < w; i++)
                {
                    int n = makeFIFO(parent, f, j, l);
                    numLinks = numLinks + n;
                }
            myMessenger.line("makeRR made "
                    + Component.plural(numLinks, " Link"));
            return numLinks;
        }// end of makeRR

    // method to form a two-Link loop around a joint.
    // used by roundRobin Joints
    static public void makeAloop(Joint J)
        {
            String nn = ":loopOF:" + J.getName();
            OneInOneOutJoint ans =  OneInOneOutJoint.please(nn, J);// the intermediate Joint
            String l1 = "loop1-for-" + J.getName();
            String l2 = "loop2-for-" + J.getName();
            Link lk1 = Link.please(l1, J);// the first Link
            Link lk2 = Link.please(l2, J);// the second Link
            // connect the loop to this Joint
            JointFactory.connect(J, ans, lk1);
            JointFactory.connect(ans, J, lk2);
            //make lk1 empty and lk2 full with value.of(1) at zeroTime
            TimedValue iv= TimedValue.please(Time.zeroTime, Value.of(1), "starter");
            lk2.initializeWith(iv);
            lk1.initializeWith(iv.drained());
            return;
        }// end of makeAloop

}//end of JointFactory
