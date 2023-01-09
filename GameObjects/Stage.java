package GameObjects;
import java.util.ArrayList;

import GameObjects.ThingsThatMove.Platform;

import java.awt.*;

public class Stage {
    private ArrayList <Platform> platforms;
    
    public Stage(){
        platforms = new ArrayList<Platform>();
    }

    public ArrayList <Platform> getPlats(){return platforms;}

    public void draw(Graphics g){
        //draw background image
    }
    
}
