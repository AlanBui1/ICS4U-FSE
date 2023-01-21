package GameObjects.Characters.Attacks;

import java.util.*;

//class to hold properties of an Attaack
//Attacks have hitboxes, a cooldown, and a number of frames 
public class Attack {
    ArrayList<Hitbox> hitboxes; //Hitboxes that this Attack will come into effect when this Attack is used
    int coolDown; //a measure of how long the Player has to wait before using another Attack after this Attack
    int numFrames; //number of frames long the Attack is
    
    public Attack(){
        hitboxes = new ArrayList<Hitbox>();
    }
    
    public ArrayList<Hitbox> getHitboxes(){return hitboxes;} //returns the hitboxes
    public int getCoolDown(){return coolDown;} //returns the coolDown
    public int getnumFrames(){return numFrames;} //returns the number of frames the Attack is

    public void addHitbox(Hitbox h){hitboxes.add(h);} //adds a Hitbox to the ArrayList of hitboxes
    public void setCoolDown(int time){coolDown = time;} //sets the coolDown time of the Attack
    public void setNumFrames(int n){numFrames = n;} //sets the number of frames the Attack is
}
