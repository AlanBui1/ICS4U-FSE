import java.awt.*;

public class BetterShooter extends BetterPlayer{
    public static final int FACTOR = 3;

    public static final double WEIGHT = 90, GRAVITY = 0.2, RUNSPD = 1.575, AIRSPD = 1.071, AIRACCEL = 0.58, FALLSPD = 2.08, JUMPFORCE = 2.8, GROUNDFRICTION = 0.114, AIRFRICTION = 0.00375;
    public static final int WIDTH = 20, HEIGHT = 30;

    public BetterShooter(double x, double y){
        super(x, y, WEIGHT*FACTOR, AIRACCEL*FACTOR, AIRFRICTION*4, GROUNDFRICTION*FACTOR, AIRSPD*FACTOR, FALLSPD*5, GRAVITY*10, RUNSPD*FACTOR, JUMPFORCE*6.5);
    }

    public void draw(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect((int)getX(), (int)getY(), WIDTH, HEIGHT);
    }
    
}
