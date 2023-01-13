package GameObjects.ThingsThatMove;
import java.awt.*;
import java.util.*;

public class Platform extends Mover{
    private int w, h, minX, minY, maxX, maxY;
    private boolean invis;

    public Platform(int xx, int yy, int ww, int hh){
        super(xx, yy);
        w = ww;
        h = hh;
        invis = false;
    }

    public Platform(int xx, int yy, int ww, int hh, int MINX, int MAXX, int MINY, int MAXY, int vx, int vy){
        super(xx, yy);
        w = ww;
        h = hh;
        minX = MINX; minY = MINY;
        maxX = MAXX; maxY = MAXY;
        setVX(vx); setVY(vy);
        invis = false;
    }

    public Platform(HashMap<String, Integer> stats){
        super(stats.get("x"), stats.get("y"));
        w = stats.get("width"); h = stats.get("height");
        minX = stats.get("minX"); maxX = stats.get("maxX");
        minY = stats.get("minY"); maxY = stats.get("maxY");
        setVX(stats.get("vx")); setVY(stats.get("vy"));
        invis = stats.get("invisible") >= 1 ? true : false;
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

    public boolean getInvis(){return invis;}

    public int getWidth(){return w;}
}
