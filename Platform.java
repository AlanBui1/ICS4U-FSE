import java.awt.*;

public class Platform {
    private int x, y, w, h;

    public Platform(int xx, int yy, int ww, int hh){
        x = xx;
        y = yy;
        w = ww;
        h = hh;
    }

    public void draw(Graphics g, Color c){
        g.setColor(c);
        g.fillRect(x, y, w, h);
    }

    public Rectangle getRect(){
        return new Rectangle(x, y, w, h);
    }

    public int getY(){
        return y;
    }
}
