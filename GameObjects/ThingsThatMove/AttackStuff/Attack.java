package GameObjects.ThingsThatMove.AttackStuff;

import java.util.*;

public class Attack {
    ArrayList<Hitbox> hitboxes; //Hitboxes that this Attack will come into effect when this Attack is used
    int coolDown; //a measure of how long the Player has to wait before using another Attack after this Attack
    int numFrames;
    
    public Attack(){
        hitboxes = new ArrayList<Hitbox>();
    }
    
    public ArrayList<Hitbox> getHitboxes(){return hitboxes;} //returns the hitboxes
    public int getCoolDown(){return coolDown;} //returns the coolDown
    public int getnumFrames(){return numFrames;}

    public void addHitbox(Hitbox h){hitboxes.add(h);} //adds a Hitbox to the ArrayList of hitboxes
    public void setCoolDown(int time){coolDown = time;} //sets the coolDown time of the Attack
    public void setNumFrames(int n){numFrames = n;}
    
}
