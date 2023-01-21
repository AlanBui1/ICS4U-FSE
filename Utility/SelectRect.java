package Utility;

import java.awt.*;

//class used for selectable rectangles
public class SelectRect {
    public Rectangle rect; 
    public int val; 
    public String name; 
    public Image img; //what to display when drawing the SelectRect

    public SelectRect(Rectangle r, int v, String s, String imgFile){
        rect = r;
        val = v;
        name = s;
        img = Util.loadScaledImg(imgFile, (int)rect.getWidth()-20, (int)rect.getHeight()-20);
    }

    public void draw(Graphics g){ //displays the SelectRect on the screen
        g.setColor(Color.WHITE);
        if (!name.equals(name.toUpperCase()) || name.equals("RANDOM")){
            g.fillRect((int)rect.getX()+5, (int)rect.getY()+5, (int)rect.getWidth()-10, (int)rect.getHeight()-10);
        }
        g.drawImage(img, (int)rect.getX()+10, (int)rect.getY()+10, null);
    }

    public boolean contains(int x, int y){ //method to check if (x, y) is contained in the SelectRect Rectangle
        return rect.contains(x, y);
    }

    public void changeImg(String fileName){ //changes the image given the file name
        img = Util.loadScaledImg(fileName, (int)rect.getWidth(), (int)rect.getHeight());
    }
}
