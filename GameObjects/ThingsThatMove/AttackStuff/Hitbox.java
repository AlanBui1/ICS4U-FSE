package GameObjects.ThingsThatMove.AttackStuff;
import java.awt.*;
import java.util.HashMap;

import GameObjects.ThingsThatMove.Mover;
import MainGame.Gamepanel;

public class Hitbox extends Mover{
    private double  width, //how wide the Hitbox is
                    height, //how tall the Hitbox is
                    time, //how long the Hitbox is active for
                    startOffsetX, //how far away in the X-direction from the top-left corner of the Player the Hitbox will be launched 
                    startOffsetY, //how far away in the Y-direction from the top-left corner of the Player the Hitbox will be launched 
                    baseKnockbackX, //base knockback in the x direction
                    baseKnockbackY, //base knockback in the y direction
                    damage; //how much damage this Hitbox does if the opponent gets hit by it

    private HashMap<String, Double> data; //HashMap of all the information needed to clone the Hitbox
    private int stunTime; //how long the Hitbox will stun the opponent for

    public Hitbox(HashMap <String, Double> stats){ //constructor that initializes fields using a HashMap with all the necessary information
        super(stats);
        
        width = stats.get("width");
        height = stats.get("height");
        time = stats.get("timeactive");
        startOffsetX = stats.get("startoffsetx");
        startOffsetY = stats.get("startoffsety");
        baseKnockbackX = (double)stats.get("basekbx");
        baseKnockbackY = (double)stats.get("basekby");
        stunTime = (int)(double)stats.get("stuntime");
        damage = stats.get("damage");

        data = stats;
    }

    //returns a new Hitbox with the same data
    public Hitbox cloneHitbox(){
        return new Hitbox(data);
    }
    
    //getter methods
    public double getTime(){return time;};
    public Rectangle getRect(){return new Rectangle((int)this.getX(), (int)this.getY(), (int)width, (int)height);}
    public double getOffsetX(){return startOffsetX;}
    public double getOffsetY(){return startOffsetY;}
    public double getWidth(){return width;}
    public double getHeight(){return height;}
    public double getKnockBackX(){return baseKnockbackX;}
    public double getKnockBackY(){return baseKnockbackY;}
    public double getDamage(){return damage;}
    public int getStun(){return stunTime;}    

    //setter methods
    public void setWidth(double W){width= W;}
    public void setHeight(double H){height = H;}
    public void setKnockBackX(double kb){baseKnockbackX = kb;}
    public void setKnockBackY(double kb){baseKnockbackY = kb;}
    public void setDamage(double d){damage = d;}
    public void addTime(double t){time += t;}

    @Override
    public void move(){
        time --; //reduces the time the Hitbox is active for
        if (time < 0){ 
            return;
        }
        super.move();
    }

    public void draw(Graphics g){ //draws the Hitbox
        g.setColor(Color.GREEN);
        g.fillRect((int)(this.getX()), (int)(this.getY()), (int)width, (int)height);
    }
}
