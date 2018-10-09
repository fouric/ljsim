/* a RoundRobinFork distributes input values to its outputs in sequence
 * see also RoundRobinJoin 
 * */
 /* Written by Ivan on 16 August 2017 */

package ljSim.jointPkg;

import ljSim.actionPkg.Action;
import ljSim.actionPkg.RoundRobinJoinAction;
import ljSim.components.Component;
import ljSim.components.Joint;
import ljSim.components.Link;

public class RoundRobinJoinJoint extends Joint {

    public static RoundRobinJoinJoint please(String name, Component parent)
        { return new RoundRobinJoinJoint(name, parent);}
    
    // the constructor
    protected RoundRobinJoinJoint(String name, Component parent)
        {
            super(name, parent);
            addAnAction(RoundRobinJoinAction.please("RoundRobinJoin", this));
            JointFactory.makeAloop(this);
            return;
        }// end of constructor
    
    public void masterClear()
        {
            super.masterClear();
            for (Action A : actions)
                A.initialize();
            }

    //to help debugging: a breakpoint here will stop when this type of joint wakes
    public boolean wakeAndDo(Link why)
        {
            return super.wakeAndDo(why);
        }
    
    public String getTypeString()
        {
            return "RoundRobinJoin";
        } // returns the type of this component
   
    //---------- topology builders ----------------------

    public void addOutputLink(Link L)
        {
                super.addAnOutputLink(L, 2);
        }

    public boolean checkMyTopology()
        {
            boolean ans= checkEnoughLinks(2, 2);
            ans= ans && super.checkMyTopology();
            return ans;
        }

}//end of class RoundRobinJoin
