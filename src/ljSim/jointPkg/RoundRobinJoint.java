/* RoundRobinJoint is intended as the base for Fork and Joint and maybe others
 * 
 * You set it up with set of copy actions - it selects which one to use.
 * 
 * Built in is a RingCopyAction to handle the ring increment task.
 * The index selects which action to ask for guard, grab, and fire.
 * 
 */
/* written by Ivan 22 December 2017 */

package ljSim.jointPkg;

import ljSim.actionPkg.Action;
import ljSim.actionPkg.CopyAction;
import ljSim.actionPkg.RingCopyAction;
import ljSim.basicA.Time;
import ljSim.basicA.TimedValue;
import ljSim.basicA.Value;
import ljSim.components.Component;
import ljSim.components.Joint;
import ljSim.components.Link;

public class RoundRobinJoint extends Joint{
    
    private String myTypeName= null;
    private int circumference= -1;
    private int numDataInLinks= -1;
    private int numDataOutLinks= -1;

    // returns the type of this component
    public String getTypeString(){return myTypeName;}

    public static RoundRobinJoint please(String name, Component parent, String typeName)
        {  return new RoundRobinJoint(name,  parent, typeName); }
        
    //constructor
    private RoundRobinJoint(String name, Component parent, String typeName)
        {
            super(name, parent);
            myTypeName= typeName;
            //makes and initializes the loop as part of constructor
            JointFactory.makeAloop(this);
            return;
        }
    
    //check that the index will select an action
    public boolean checkIndexBounds(int index)
    {
        if (index < 1 )return false;
        if ( index > actions.size() -1) return false;
        return true;
    }

    //this puts in the actions for 
    // the data sizes (control I/O not included)
    public void addActions(int in, int out)
        {//
            numDataInLinks= in;
            numDataOutLinks= out;
            //everyone needs the feedback loop
            actions.add(RingCopyAction.please("ring", this, 0, 0));
            //now add the copy actions from or to link 1
            if(in > out) 
                {// it's a Join type
                    for (int i= 1; i <= in; i++)
                        actions.add(CopyAction.please("inAction" + i, this, i, 1));
                    myTypeName= "RoundRobinJoint";
                }
            else
                {// it's a Fork type
                    for (int i= 1; i <= out; i++) 
                        actions.add(CopyAction.please("outAction" + i, this, 1, i));
                    myTypeName= "RoundRobinFork";
                }
            
        }

    public void masterClear()
        {
            super.masterClear();
            TimedValue tv= TimedValue.please(Time.zeroTime, Value.of(1), "initial value");
            this.getInLink(0).initializeWith(tv);
            this.getOutLink(0).initializeWith(tv.drained());
            int circI= this.getInputDrainCommands().size();
            int circO= getOutputFillCommands().size();
            circumference = circI + circO - 3;
            for(Action A : this.actions)
                A.initialize();
        }

    public boolean checkMyTopology()
        {
            boolean ans= checkEnoughLinks(numDataInLinks + 1, numDataOutLinks + 1);
            ans= ans && super.checkMyTopology();
            return ans;
        }

    //to help debugging: a breakpoint here will stop when this type of joint wakes
    public boolean wakeAndDo(Link why)
        {
            this.wakeReport(why);
            RingCopyAction ringAction= (RingCopyAction)actions.get(0);
            Time ringTime=  ringAction.guard();
            if(ringTime == null)return false; //exit if the ring isn't ready
            //we have a ring value

            int index= ringAction.getRingIndexValue().getMyValue();
            if(checkIndexBounds(index) == false)
                myMessenger.error("index out of bounds for " + this.getFullName());
            CopyAction chosenAction= (CopyAction)actions.get(index);
            String s= "chosenAction is " + chosenAction.copyActionString();
            
            Time dataTime= chosenAction.guard();
            if(dataTime == null)return false;
            
            //we are able to move data
            Time actionTime= Time.latestOf(ringTime, dataTime);
            String ss= " CopyActoin " + chosenAction.getCombinedName() + " can act ";
            myMessenger.line(ss);
            // now do all of the grabs()
            ringAction.grab(actionTime);
            chosenAction.grab(actionTime);
            
            //now we can fire
            incUseCount();
            ringAction.fire();
            chosenAction.fire();
            return true;
        }//end of wakeAndDo

}//end of class MemoryLoopJoint
