package GameObjects.ThingsThatMove;
import java.awt.*;
import java.util.*;

//class for Platforms 
//extends Mover class since Platforms can move
public class Platform extends Mover{
    private int width, //width of the rectangular Platform
                height, //height of the rectangular Platform
                minX, //minimum x-coordinate of the Platform
                minY, //minimum y-coordinate of the Platform
                maxX, //maximum x-coordinate of the Platform
                maxY; //maximum y-coordinate of the Platform

    private boolean invis; //true if the AI can NOT detect this Platform 
    
    public Platform(HashMap<String, Integer> stats){ //constructor uses HashMap to load values
        super(stats.get("x"), stats.get("y"));
        width = stats.get("width"); height = stats.get("height");
        minX = stats.get("minX"); maxX = stats.get("maxX");
        minY = stats.get("minY"); maxY = stats.get("maxY");
        setVX(stats.get("vx")); setVY(stats.get("vy"));
        invis = stats.get("invisible") == 0 ? false : true; //a value of 0 means not invisible, anything else corresponds to invisible
    }

    @Override
    public void move(){ //moves the Platform
        if (!(minX <= getX() + getVX() && getX() + getVX() <= maxX)){ //check if moving will put Platform out of the right range in the X
            setVX(-getVX()); //moves in the other direction
        }
        if (!(minY <= getY() + getVY() && getY() + getVY() <= maxY)){ //check if moving will put Platform out of the right range in the Y
            setVY(-getVY()); //moves in the other direction
        }

        super.move();
    }

    public void draw(Graphics g, Color c){ //draws the Platform
        g.setColor(c);
        g.fillRect((int)getX(), (int)getY(), width, height);
    }

    public Rectangle getRect(){ //returns the Rectangle area where the Platform is
        return new Rectangle((int)getX(), (int)getY(), width, height);
    }

    public boolean getInvis(){return invis;} //returns if the Platform can't/can be detected by the AI

    public int getWidth(){return width;} //returns width of the Platform

    public Point getCenterPoint(){return new Point((int)getX() + width/2, (int)getY() + height/2);} // returns the point in the center of the Platform
}
