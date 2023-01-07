package AttackStuff;
import java.awt.*;
import java.util.HashMap;

import ThingsThatMove.Mover;

public class Hitbox extends Mover{
    private double w, h, time;
    private double startOffsetX, startOffsetY;
    private Force knockBack;

    public Hitbox(HashMap <String, Double> stats){
        super(-1,-1, stats.get("vx"), stats.get("vy"), stats.get("ax"), stats.get("ay"));

        w = stats.get("width");
        h = stats.get("height");
        time = stats.get("timeactive");
        startOffsetX = stats.get("startoffsetx");
        startOffsetY = stats.get("startoffsety");
        // knockBack = new Force() TO DO FINISH THIS with knockback growth and base knockback
        knockBack = new Force((double)stats.get("basekbx"), (double)stats.get("basekby"), (int)time, (int)(double)stats.get("stuntime")); 
    }

    public Hitbox(double X, double Y, double W, double H, double VX, double VY, double AX, double AY, double T, double knockBackX, double knockBackY, int knockBackTime, int stunTime){
        super(X, Y, VX, VY, AX, AY);

        w = W; h = H;
        time = T;
        knockBack = new Force(knockBackX, knockBackY, knockBackTime, stunTime);
    }

    public Hitbox cloneHitbox(){
        return new Hitbox(getX(), getY(), getWidth(), getHeight(), getVX(), getVY(), getAX(), getAY(), getTime(), knockBack.getMX(), knockBack.getMY(), knockBack.getTime(), knockBack.getStun());
    }
    
    public double getTime(){return time;};
    public Rectangle getRect(){return new Rectangle((int)this.getX(), (int)this.getY(), (int)w, (int)h);}
    public Force getForce(){return knockBack;}
    public double getOffsetX(){return startOffsetX;}
    public double getOffsetY(){return startOffsetY;}
    public double getWidth(){return w;}
    public double getHeight(){return h;}
    public double getKnockBackX(){return knockBack.magnitudeX;}
    public double getKnockBackY(){return knockBack.magnitudeY;}

    public void addTime(double t){
        time += t;
    }
    public int getStun(){
        return knockBack.getStun();
    }

    public void setKnockBackX(double kb){knockBack.setMX(kb);}
    public void setKnockBackY(double kb){knockBack.setMX(kb);}

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
        g.fillRect((int)this.getX(), (int)this.getY(), (int)w, (int)h);
    }
}
