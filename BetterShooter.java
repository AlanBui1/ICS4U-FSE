import java.awt.*;

public class BetterShooter extends BetterPlayer{
    public static final int FACTOR = 3;

    //modeled after https://ultimateframedata.com/lucina
    //JUMPFORCE is from Brawl Marth

    public static final double WEIGHT = 90, GRAVITY = 0.075, RUNSPD = 1.575, AIRSPD = 1.071, AIRACCEL = 0.08, FALLSPD = 1.58, JUMPFORCE = 2.4, GROUNDFRICTION = 0.114, AIRFRICTION = 0.00375;
    public static final int WIDTH = 20, HEIGHT = 30;

    public BetterShooter(double x, double y){
        super(x, y, WEIGHT*FACTOR, AIRACCEL*FACTOR, AIRFRICTION*FACTOR, GROUNDFRICTION*FACTOR, AIRSPD*FACTOR, FALLSPD*FACTOR, GRAVITY*FACTOR, RUNSPD*FACTOR, JUMPFORCE*FACTOR);
    }

    public void draw(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect((int)getX(), (int)getY(), WIDTH, HEIGHT);
    }
    
}
