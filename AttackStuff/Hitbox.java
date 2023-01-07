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
        // knockBack = new Force() TO DO FINISH THIS with knockback growth and base knockback
        //startoffset 
    }

    public Hitbox(double X, double Y, double W, double H, double VX, double VY, double AX, double AY, double T, double knockBackX, double knockBackY, int knockBackTime, int stunTime){
        super(X, Y, VX, VY, AX, AY);

        w = W; h = H;
        time = T;
        knockBack = new Force(knockBackX, knockBackY, knockBackTime, stunTime);
    }
    
    public double getTime(){return time;};
    public Rectangle getRect(){return new Rectangle((int)this.getX(), (int)this.getY(), (int)w, (int)h);}
    public Force getForce(){return knockBack;}

    public void addTime(double t){
        time += t;
    }
    public int getStun(){
        return knockBack.getStun();
    }

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
