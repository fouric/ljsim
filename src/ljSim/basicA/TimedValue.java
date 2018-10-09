/* TimedValue is a class for a value that carries a Time as well as its data and a comment.
 * Formerly called TValue it got its full name on 18 November 2017.
 * TimedValue are passed to a Link or to a Joint as parameter of a Command
 * So Commands will carry parameters. 
 * That makes the process of filling a Link a single Commmand rather than a call to fillMe().
 */
/* Written by Ivan Sutherland on 3 September 2017 */
/* Updated by Ivan Sutherland on18 November 2017 */

package ljSim.basicA;

public class TimedValue 
{
    private Time theTime= null;
    private Value theValue= null;
    public String comment;
    
    public Time getTime(){return theTime;}
    public Value getValue(){return theValue;}
    public String getComment(){return comment;}
    public boolean hasValue(){return theValue!= null;}

    //Constructor
    private TimedValue (Time t, Value v, String s)
        {
            theValue= v;
            theTime= t;
            comment= s;
        }
    
    static public TimedValue please(Time t, Value v, String s)
        {
            TimedValue ans= new TimedValue(t,v,s);
            return ans;
        }
    
    static public TimedValue please(Time t, Value v)
        {
            TimedValue ans= new TimedValue(t,v, "unknown");
            return ans;
        }
        
    public TimedValue replaceValue(Value v)
        {
            TimedValue ans= TimedValue.please(theTime, v, "new Value");
            return ans;
        }
    
    public boolean isEqualTo(TimedValue tv)
        {
            if( ! theTime.isSameAs(tv.theTime))
                    return false;
            if(theValue == null && tv.theValue == null)
                return true;
            if(this.theValue == tv.theValue)
                return true;
            return false;
        }
    
    public boolean isBefore(TimedValue tv)
        {
            boolean ans= getTime().isBefore(tv.getTime());
            return ans;
        }
    
    public boolean isAfter(TimedValue tv)
        {
            boolean ans= getTime().isAfter(tv.getTime());
            return ans;
        }
    
    //get an empty TValue with time stamp t
    static public TimedValue please(Time t)
        {
            TimedValue ans= new TimedValue(t, null, "drained");
            return ans;
        }
    
    public static TimedValue earlierOf(TimedValue p, TimedValue q)
        {
            Time tp= p.getTime();
            Time tq= q.getTime();
            if(tp.isBefore(tq))
                return p;
            else
                return q;
        }
    
    public static TimedValue laterOf(TimedValue p, TimedValue q)
        {
            Time tp= p.getTime();
            Time tq= q.getTime();
            if(tp.isAfter(tq))
                return p;
            else
                return q;
        }
    
    public TimedValue butAfter(Time t)
        {
            if(t ==  null)
                return this;
            Time f= getTime();
            Time x= f.butAfter(t);
            TimedValue ans= please(x, theValue, comment);
            return ans;
        }
    
    public TimedValue delayedBy(int d)
    {
        //could make a check here for positive delay d
        TimedValue ans= please(theTime.delayedBy(d), theValue, comment);
        return ans;
    }
   
    //get a new TimedValue like this one but with null value
    public TimedValue drained()
        {
            TimedValue ans= please(theTime, null, "drained");
            return ans;
        }
    
    //change the value in THIS TimedValue to null
    public void drainMyValue(){theValue= null;}
    
    public TimedValue drainedAndAfter(Time t)
        {
           TimedValue ans= please(theTime.butAfter(t), null, "drained");
           return ans;
        }
    
    public String valueString()
    {
        if (theValue == null) return " empty";
        int i= this.getValue().getMyValue();
        return Integer.toString(i);
    }
    
    public String getMyString()
        {
            String ans;
            if(theValue != null)
                ans= theValue.getString() + theTime.atTimeString();
            else
                ans= " void " + theTime.atTimeString();
            return ans;
        }
    
    public TimedValue nextRingValue(int modulus)
        {
            Value newValue= theValue.nextRingValue(modulus);
            TimedValue ans= TimedValue.please(theTime, newValue, comment);
            return ans;
        }
    
    public TimedValue setValue(int x)
        {
            Value v= Value.of(x);
            return setValue(v);
        }

    public TimedValue setValue(Value v)
        {
            TimedValue ans= TimedValue.please(theTime, v, comment);
            return ans;
        }
    
}//end of TimedValue class
