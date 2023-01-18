package Utility;

import java.awt.*;

//class used for selectable rectangles
public class SelectRect {
    public Rectangle rect;
    public int val;
    public String name;
    public Image img;

    public SelectRect(Rectangle r, int v, String s, String imgFile){
        rect = r;
        val = v;
        name = s;
        img = Util.loadScaledImg(imgFile, (int)rect.getWidth(), (int)rect.getHeight());
    }

    public void draw(Graphics g){
        g.drawImage(img, (int)rect.getX(), (int)rect.getY(), null);
    }

    public boolean contains(int x, int y){
        return rect.contains(x, y);
    }
}
