package GameObjects;
import java.util.ArrayList;

import GameObjects.ThingsThatMove.Platform;
import MainGame.Gamepanel;

import java.awt.*;

public class Stage {
    private ArrayList <Platform> platforms; //platforms that make up the stage
    private Platform mainPlatform; //the main platform that the AI will follow
    
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

    public ArrayList <Platform> getPlats(){return platforms;} //method to return the Platforms in this Stage
    public Platform getMainPlat(){return mainPlatform;} //method to return the main platform of the Stage

    public void draw(Graphics g){ //draws the Stage and the Platforms in the Stage
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Gamepanel.WIDTH, Gamepanel.HEIGHT);

        for (Platform p : platforms){
            p.draw(g, Color.GRAY);
        }
    }
}