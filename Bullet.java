import java.awt.*;

public class Bullet {
    private double x, y;
    private int v, dir;

    // add "type" parameter later? to store who shot the bullet 
    public Bullet(int vel, double xBul, double yBul, int direct){
        x = xBul;
        y = yBul;
        v = vel;
        dir = direct;
    }

    public void move(){
        x += dir*v;
    }

    public double x(){ // returns x position 
        return x;
    }

    public double y(){ // returns y position 
        return y;
    }

    public Rectangle getRect(){ // returns Rectangle
        return new Rectangle((int)x, (int)y, 10, 3);
    }

    public void draw(Graphics g){ // draws bullet onto screen 
        g.setColor(Color.WHITE);
        g.fillRect((int)x, (int)y, 10, 3);
    }
}
