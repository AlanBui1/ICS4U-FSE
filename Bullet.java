import java.awt.*;
import java.util.ArrayList;


public class Bullet {
    private double x, y;
    private Player player, oppo;
    private ArrayList<Vector> accelX;

    // add "type" parameter later? to store who shot the bullet 
    public Bullet(int vel, double xBul, double yBul, int direct, Player playerShooting, Player playerOppo){
        accelX = new ArrayList<Vector>();
        x = xBul;
        y = yBul;
        player = playerShooting;
        oppo = playerOppo;
    }

    public void move(){
        x += accelX.get(0).getMagnitude();
    }

    // change to boolean not void when it actually does smthing
    public boolean checkHitPlayer(ArrayList<Bullet> bullets){ // returns which alien (if any) is hit by bullet
        if ((oppo.getRect()).contains(x,y)){
            return true;
        }
        return false;
    }

    public void addAccel(String name, double magnitude, int time, String dir){
        if (dir == "X") accelX.add(new Vector(name, magnitude, time));
    }

    public double x(){ // returns x position 
        return x;
    }

    public double y(){ // returns y position 
        return y;
    }

    public Player getOppo(){
        return oppo;
    }

    public Rectangle getRect(){ // returns Rectangle
        return new Rectangle((int)x, (int)y, 7, 2);
    }

    public void draw(Graphics g){ // draws bullet onto screen 
        g.setColor(Color.WHITE);
        g.fillRect((int)x, (int)y, 7, 2);
    }
}
