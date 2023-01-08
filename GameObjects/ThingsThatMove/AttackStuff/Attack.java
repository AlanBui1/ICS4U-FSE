package GameObjects.ThingsThatMove.AttackStuff;

import java.util.*;

public class Attack {
    ArrayList<Hitbox> hitboxes;
    int coolDown;

    public Attack(){
        hitboxes = new ArrayList<Hitbox>();
    }
    
    public ArrayList<Hitbox> getHitboxes(){return hitboxes;}
    public void setCoolDown(int t){coolDown = t;}
    public int getCoolDown(){return coolDown;}
    public void addHitbox(Hitbox h){
        hitboxes.add(h);
    }
}
