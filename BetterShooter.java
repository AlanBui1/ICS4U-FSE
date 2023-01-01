import java.awt.*;

public class BetterShooter extends BetterPlayer{
    public static final int FASTSIDEATK = 0, FASTUPATK = 1, FASTDOWNATK = 2;
    public static final double [] BASEKBX = {2, 0, 0}, BASEKBY = {0, -10, 10}, KBGROWTH = {0, 0 ,0};

    /*
     baseKBx and baseKBy are the base knockback in the x and y directions, respectively
     KBGROWTH is a measure of how much the knockback increases as opponents' damage increases #TO DO 
    */

    public static final double WEIGHT = 90, GRAVITY = 2, RUNSPD = 5, AIRSPD = 3.6213, AIRACCEL = 1.73, FALLSPD = 10.4, JUMPFORCE = 20.02, GROUNDFRICTION = 0.85, AIRFRICTION = 0.65;
    public static final int WIDTH = 20, HEIGHT = 30;

    public BetterShooter(double x, double y){
        super(x, y, WEIGHT, AIRACCEL, AIRFRICTION, GROUNDFRICTION, AIRSPD, FALLSPD, GRAVITY, RUNSPD, JUMPFORCE);
    }

    public void attack(boolean [] keys){
        if (getCoolDown() <= 0){
            if (keys[getFastKey()]){
                if (keys[getUKey1()]){
                    fastUpAtk();
                }
                else if (keys[getDKey()]){
                    fastDownAtk();
                }
                else{
                    fastSideAtk();
                }
            }
        }
    }

    public void fastSideAtk(){ //stun gun
        Force knockForce = new Force((BASEKBX[FASTSIDEATK])*getDir(), 0, 1, 10);
        Mover movt = new Mover(getX(), getY(), 15*getDir(), 0);
        addHitBox(movt, knockForce, 7, 7, 15);
        setCoolDown(15);
    }

    public void fastUpAtk(){
        addHitBox(getX(), getY(), 20.0, 20.0, 0, -10, 0, 0, 30, 0, -20, 100);
        setCoolDown(30);
    }

    public void fastDownAtk(){
        addHitBox(getX(), getY(), 7.0, 7.0, 0, 10, 0, 0, 30, 0, 20, 100);
        setCoolDown(30);
    }

    

    public void draw(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect((int)getX(), (int)getY(), WIDTH, HEIGHT);
    }
    
}
