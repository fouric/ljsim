/* A Link issues LincCommands to connect it to Joints.
 * Links have two types, FillCommmand and DrainCommand
 * When asked for their target they return type Link
 */
/* Written by Ivan 25 August 2017 */

package ljSim.commands;


import ljSim.basicA.Time;
import ljSim.basicA.TimedValue;
import ljSim.components.Link;

abstract public class LinkCommand extends UnsortedCommand {

    abstract public void doIT(); // will differ for different subComands
    abstract public void grabIT(TimedValue tv); // this method will return always, never queue
    // must do grabIT for everyone before doing any doIT commands.

    //Constructor
    protected LinkCommand(Link L)
        {
            super (L);
        }

    public Link getTarget(){return (Link) super.getTarget();}
    
    public boolean enQueueMe(Time d)
        {
            this.setTime(d);
            boolean b= super.enQueueMe(d);
            return b;
        }
    
    //use these for guards
    public Time linkBecameFullAt() { return getTarget().becameFullAt();}
    public Time linkBecameEmptyAt(){ return getTarget().becameEmptyAt();}
    public TimedValue getLinkOutputValue(){return getTarget().getOutput();}
    
    @Override
    protected String getMyType(){return "LinkCommand";}

}//end of class LinkCommand
