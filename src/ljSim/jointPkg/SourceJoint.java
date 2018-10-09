/* Class sourceJoint is a joint with only output links.
 * It refills its output link a fixed number of times with some kind of data.
 * Written by Ivan on 3 June 2017
 * Updated by Ivan on 12 August 2017
 */

package ljSim.jointPkg;


import ljSim.actionPkg.SourceAction;
import ljSim.actionPkg.Action;
import ljSim.basicA.Time;
import ljSim.commands.SortedJointCommand;
import ljSim.components.Component;
import ljSim.components.Joint;
import ljSim.components.Link;

public class SourceJoint extends Joint {
    
    public static SourceJoint please(String name, Component parent)
        { return new SourceJoint(name, parent); }

    // the constructor
    protected SourceJoint(String name, Component parent) {
        super(name, parent);
        //get a source action with period of 100
        Action x= SourceAction.please("sourceFill", this, 100);
        addAnAction(x);
        return;
    }// end of constructor

    public String getTypeString(){return "SourceJoint";}
    
    public void makeStartTask(Time t)
        {
            String s= timeHerald(t) + " makes a start task";
            myMessenger.line(s);
            SortedJointCommand myWakeUp= makeAwakeUpCommand();
            myWakeUp.setSource(null);
            myWakeUp.setTime(t);
            myWakeUp.enQueueMe(t);
        }//end of start
    ///firing
    public SortedJointCommand makeAwakeUpCommand()
        {
            SortedJointCommand c= new WakeUpCommand(this);
            return c;
        }

   private class WakeUpCommand extends SortedJointCommand{
        private WakeUpCommand(Joint j){ super (j);}
        
        public void doIT()
        {
            Joint J= getTarget();
            J.wakeAndDo(getSource());
            return;
        }
        
        public String getMyType() { return ("SortedWakeUpCommand"); }
    }// end of class WakeUp
    

    //---------- topology builders ----------------------
    public void addAnInputLink(Link lk)
        { super.addAnInputLink(lk, 0); }
    


}// end of SourceJoint
