package GameObjects;
import java.util.ArrayList;

import GameObjects.ThingsThatMove.Platform;

import java.awt.*;

public class Stage {
    private ArrayList <Platform> platforms;
    private Platform mainPlatform;
    
    public Stage(){
        platforms = new ArrayList<Platform>();
    }
    
    public Stage(ArrayList <Platform> plats){
        platforms = new ArrayList<Platform>();
        for (Platform p : plats){
            platforms.add(p);
        }
        mainPlatform = platforms.get(0);
    }

    public ArrayList <Platform> getPlats(){return platforms;}

    public void draw(Graphics g){
        //draw background image
    }
    public Platform getMainPlat(){return mainPlatform;}
}