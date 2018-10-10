/*
 * The Arbiter class is deprecated The Arbiter class implement the
 * first-come-first-served operation.
 * 
 * 
 * The Arbiter class implements a first-cone-first-served function. The hard
 * part is that we may be simulating one thread of activity ahead of another. So
 * we can't really tell which input came first until we are sure the tardy
 * simulation has caught up with a more prompt simulation
 * 
 */

//package ljSim.parts;

//import ljSim.basicA.Delay;
//import ljSim.basicB.Component;
//import ljSim.commands.Command;
/*
 * 
 * public class Arbiter extends Component{ // private int useCount = 0; private
 * When firstWhen = null; // Time of first arrival private When xWhen= null;
 * private When yWhen= null; private int last=0; //0 if X was last, 1 if Y was
 * last //commands to do when you decide a winner private Command doWinnerX =
 * null; private Command doWinnerY = null;
 * 
 * private static int aribterNumber= 1;// for making names
 * 
 * 
 * public void masterClear() { last = 0; xWhen= null; yWhen= null; }
 * 
 * public String getTypeString(){return "Arbiter ";}
 * 
 * // the constructor public Arbiter(String name, Component parent) { super
 * (parent); String newName= nameMaker (name); setName(newName);
 * 
 * return; }//end of constructor
 * 
 * private String nameMaker(String name) { // create a name for this Link String
 * nn= (name == null) ? "" : "-" + name; int n= aribterNumber; String ans= "L:"
 * + n + nn; aribterNumber ++ ; return ans; }
 * 
 * 
 * // this method receives input X public void dingX(Delay d) { if (xWhen !=
 * null) myMessenger.error("double ding input X of " + getName()); xWhen =
 * Chore.getWhen(d); gotOne(xWhen); return; }// end of dingX
 * 
 * // this method receives input X public void dingY(Delay d) { if (yWhen !=
 * null) myMessenger.error("double ding input Y of " + getName()); yWhen = xWhen
 * = Chore.getWhen(d); gotOne(yWhen); return; }// end of dingY
 * 
 * //this method tests to see if both there public void gotOne (When w) {
 * firstWhen = w; if((xWhen == null)||(yWhen==null)) return; //got only one ding
 * 
 * 
 * 
 * 
 * }//end of input A
 * 
 * //this method runs later //all arbiter inputs must have arrived public void
 * dong(Delay d) { last= last ^ 1; //switch in case it's contested //if no X or
 * X was last if(xWhen == null) { yWins(d); return; } if(yWhen == null) {
 * xWins(d); return; } //both are present, do least recent one
 * if(xWhen.startsBefore(yWhen)) {//X got there first so do it xWins(d); return;
 * } if(yWhen.startsBefore(xWhen)) {//y is first, do y yWins(d); ; } //no clear
 * winner, so do the other one if(last == 1) { xWins(d); return; } yWins(d);
 * return; } //end of dong private void xWins(Delay d) { last = 1;
 * doWinnerX.DO(d); }
 * 
 * private void yWins(Delay d) { last = 0; doWinnerY.DO(d); }
 * 
 * 
 * //The WinCommand checks who won the arbitration private class WinCommand
 * extends Command { //constructor private WinCommand (Arbiter A){ super (A);
 * }//end of WinCommand
 * 
 * public String getMyType() { return ("WinCommand"); }
 * 
 * public void doIT(Delay d) { Component T= getTarget(); Arbiter A= (Arbiter)T;
 * A.dong(d); }// end of DO
 * 
 * } //end of FillCommand Class
 * 
 * }//End of class arbiter
 */
