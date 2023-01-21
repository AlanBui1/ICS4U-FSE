package GameObjects.Stages;
import java.util.ArrayList;
import javax.swing.*;

import MainGame.Gamepanel;
import Utility.Util;

import java.awt.*;

//class that stores details about the stage such as how to display it on the screen
//Stage class can draw Images for Platforms on the screen
public class Stage {
    private ArrayList <Platform> platforms; //platforms that make up the stage
    private Platform mainPlatform; //the main platform that the AI will follow
    private Image bg, smallPlat, mainPlat;
    
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
        g.drawImage(bg,0,0, Gamepanel.WIDTH, Gamepanel.HEIGHT, null);

        for (Platform p : platforms){
            if (!p.equals(mainPlatform)){
                p.draw(g, smallPlat);
            }
            else{
                 p.draw(g, mainPlat);
            }
        }
        mainPlatform.draw(g, mainPlat);
    }

    public void setElements(String fileName){
        bg = Util.loadImg("assets/" + fileName);
        smallPlat = Util.loadImg("assets/smallPlatform.png");
        mainPlat = Util.loadImg("assets/mainPlatform.png");
    }
}

