/* A DemandMergeJoint issues SortedJointCommand to connect it to Links. */
/* Written by Ivan 25 August 2017 */

 /* Joints have only one thing to do, WakeUp
  * A SortedJointCommand never actually does the task immediately
  * Instead it puts itself on a queue to be done in sequence. 
  * Can only be done as it comes off the queue.
  * 
  * When asked for their target a SortedJointCommand returns type Joint
  */

package ljSim.commands;

import java.util.Queue;
import java.util.LinkedList;


import ljSim.basicA.Time;
import ljSim.components.Joint;

public class SortedJointCommand extends JointCommand
    {
        private Queue<UnsortedCommand> doBefore= null; 
        public Queue<UnsortedCommand> getMyDeque() {return doBefore;}
        public boolean addToDeque(UnsortedCommand u){return doBefore.add(u);}
        public boolean addToDeque(Queue<UnsortedCommand> q){return doBefore.addAll(q);}
        public int getMyQueueSize() {return doBefore.size();}
       
        public Queue<UnsortedCommand>takeMyQueue()
        {
            Queue<UnsortedCommand>ans= doBefore;
            if(ans == null)return null;
            doBefore.clear();
            return ans;
        }

       static public SortedJointCommand please(Joint J){return new SortedJointCommand(J);}

       
    // Constructor
    public SortedJointCommand(Joint J)
        {
            super(J);
            doBefore= new LinkedList<UnsortedCommand>();
            }
        
    //get a sub task, if any, from my doBefore queue and remove it
    public UnsortedCommand takeAsubTask()
        {
            if(doBefore == null)return null;
            UnsortedCommand c= doBefore.poll(); //get and remove first item
            return c;
        }

    public boolean enQueueMe()
        {
            boolean ans= sq.enQueue(this);
            return ans;
        }
    
    public boolean enQueueMe(Time t)
        {
            setTime(t);
            boolean ans= sq.enQueue(this);
            return ans;
        }

    @Override
    protected String getMyType()
        {
            return "SortedJointCommand";
        }//end of getMyType
    
    public void printMe()
        {
            String s= getName() + " has " + doBefore.size() +
                " sub-tasks " + this.getTime().atTimeString();
            myMessenger.line(s);
            if(doBefore.size() == 0)return;
            for(UnsortedCommand uc : doBefore)
                uc.printMe();
        }
    


}//end of SortedCommand
