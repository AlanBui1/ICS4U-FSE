package GameObjects.ThingsThatMove.AttackStuff;
import java.awt.*;
import java.util.HashMap;

import GameObjects.ThingsThatMove.Mover;
import MainGame.Gamepanel;

public class Hitbox extends Mover{
    private double w, h, time;
    private double  startOffsetX, 
                    startOffsetY,
                    baseKnockbackX,
                    baseKnockbackY,
                    damage;

    private HashMap<String, Double> data; 
    private int stunTime;

    public Hitbox(HashMap <String, Double> stats){
        super(stats);
        
        w = stats.get("width");
        h = stats.get("height");
        time = stats.get("timeactive");
        startOffsetX = stats.get("startoffsetx");
        startOffsetY = stats.get("startoffsety");
        baseKnockbackX = (double)stats.get("basekbx");
        baseKnockbackY = (double)stats.get("basekby");
        stunTime = (int)(double)stats.get("stuntime");
        damage = stats.get("damage");

        data = stats;
    }

    // TO DO USE MORE HASHMAPS

    public Hitbox cloneHitbox(){
        return new Hitbox(data);
    }
    
    public double getTime(){return time;};
    public Rectangle getRect(){return new Rectangle((int)this.getX(), (int)this.getY(), (int)w, (int)h);}
    public double getOffsetX(){return startOffsetX;}
    public double getOffsetY(){return startOffsetY;}
    public double getWidth(){return w;}
    public double getHeight(){return h;}
    public double getKnockBackX(){return baseKnockbackX;}
    public double getKnockBackY(){return baseKnockbackY;}
    public double getDamage(){return damage;}

    public void addTime(double t){
        time += t;
    }
    public int getStun(){
        return stunTime;
    }

    public void setWidth(double W){w = W;}
    public void setHeight(double H){h = H;}
    public void setKnockBackX(double kb){baseKnockbackX = kb;}
    public void setKnockBackY(double kb){baseKnockbackY = kb;}

    @Override
    public void move(){
        time --;
        if (time < 0){
            return;
        }
        super.move();
    }

    public void draw(Graphics g){
        g.setColor(Color.GREEN);
        g.fillRect((int)(this.getX()), (int)(this.getY()), (int)w, (int)h);
    }
}
