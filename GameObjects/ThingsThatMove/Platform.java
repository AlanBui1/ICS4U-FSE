package ThingsThatMove;
import java.awt.*;

public class Platform extends Mover{
    private int w, h, minX, minY, maxX, maxY;

    public Platform(int xx, int yy, int ww, int hh){
        super(xx, yy);
        w = ww;
        h = hh;
    }

    @Override
    public void move(){
        if (!(minX <= getX() + getVX() && getX() + getVX() <= maxX)){ //check if moving will put Platform out of the right range in the X
            setVX(-getVX()); //moves in the other direction
        }
        if (!(minY <= getY() + getVY() && getY() + getVY() <= maxY)){ //check if moving will put Platform out of the right range in the Y
            setVY(-getVY()); //moves in the other direction
        }

        super.move();
    }

    public void draw(Graphics g, Color c){
        g.setColor(c);
        g.fillRect((int)getX(), (int)getY(), w, h);
    }

    public Rectangle getRect(){
        return new Rectangle((int)getX(), (int)getY(), w, h);
    }
}
