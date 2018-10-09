/* The BroadcastAction class checks that all inputs are full and all outputs are empty
 * and upon firing drains its input links and fills its output links.
 * The question of what data it passes on remains unspecified.
 * Written by Ivan 13 August 2017 */
/* updated by Ivan 15 August 2017 */
/* updated by Ivan 19 December 2017 */

package ljSim.actionPkg;



import ljSim.basicA.Time;
import ljSim.commands.LinkCommand;
///import ljSim.basicA.Delay;
import ljSim.components.Joint;

public class BroadcastAction extends Action{
    
    public BroadcastAction(String name, Joint parent)
        {
            super (name, parent);
        }
    
    public void initialize(){return ;}

    //find the time at which the last input filled
    protected Time guardInputLinks()
        {
            Time doneTime= null;
            for(LinkCommand C : getMyJoint().getInputDrainCommands())
                {
                    Time fullTime= C.linkBecameFullAt();
                    if(fullTime == null )
                        {//print who is not full
                            myMessenger.line("Link " + C.getTargetName() + " is not full");
                            return null;//someone wasn't ready
                        }
                    // the Link is full, but at what time?
                    doneTime= fullTime.butAfter(doneTime);
                }
            return doneTime;
        }

    //find the time at which the last output filled
    protected Time guardOutputLinks()
        {
            Time doneTime= null;
            for(LinkCommand C : getMyJoint().getOutputFillCommands())
                {
                    Time emptyTime= C.linkBecameEmptyAt();
                    if(emptyTime == null )
                        {//print who is not full
                            myMessenger.line("Link " + C.getTargetName() + " is not full");
                            return null;//someone wasn't ready
                        }
                    // the Link is full, but at what time?
                    doneTime= emptyTime.butAfter(doneTime);
                }
            return doneTime;
        }

    //a Source is ready if all output links are empty
    public Time guard()
        {
            Joint J= getMyJoint();
            Time fullTime= guardInputLinks();
            if(fullTime == null)
                {
                    String s= "Joint " + J.getName() 
                    + " BroadcastAction guard fails because of an EMPTY input ";
    //                myMessenger.line(s);
                    return null;
                }            
            Time emptyTime= guardOutputLinks();
            if(emptyTime == null)
                {
                    String s= "Joint " + J.getName() 
                    + " BroadcastAction guard fails because of a FULL output " ;
   //                 myMessenger.line(s);
                    return null;
                }

            Time ready= Time.lastOf(emptyTime, fullTime);

            String s= J.timeHerald(ready) + " BroadcastAction guard OK";
    //        myMessenger.line(s);
            setMyGuardTime(ready);
            return ready;
        }//end of guard
    
    public void grab(Time t)
        {
            //Find when the action is possible (if at all)
            Joint J= getMyJoint();
            Time actTime= guard();
            actTime= actTime.butAfter(t);
            //get the Link commands
            int numInputs= J.getInputDrainCommands().size();
            int numOutputs= J.getOutputFillCommands().size();
            if(numInputs != numOutputs)
                {
                    String s= "Joint " + J.getName() 
                    + " BroadcastAction grab fails because unequal num inputs and outputs " ;
                   myMessenger.line(s);
                    return;
                }
            //now move the inputs to the outputs one by one

            for(int i= 0; i<J.getInputDrainCommands().size(); i++)
                {  //get the value to move
                    LinkCommand inCom= J.getInCmd(i);
                    LinkCommand outCom= J.getOutCmd(i);
                    grab(inCom, outCom, t);
                }
            return;
        }
    
    public boolean fire()
        {
            Joint J= getMyJoint();
            Time ft= guard();
            fireMessage(ft);
            //now transfer values from input to output
            for(int i= 0; i<J.getInputDrainCommands().size(); i++)
                {
                    J.getInCmd(i).doIT();
                    J.getOutCmd(i).enQueueMe(ft);
                }
            return true;
        }

}// end of class BoadcastAction
