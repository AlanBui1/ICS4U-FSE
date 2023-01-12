package GameObjects.ThingsThatMove;

import java.util.HashMap;
import Utility.Util;
import GameObjects.ThingsThatMove.AttackStuff.*;
import GameObjects.Stage;

public class ShooterAI extends Player{
    private int [] movementKeys = new int[4];
    int counter, curInd;
    public ShooterAI(double xx, double yy, HashMap <String, Double> stats, HashMap <String, Attack> atks){
        super(xx, yy, stats, atks);
        counter = 0;
        curInd = 0;
    }

    public void setKeys(){
        movementKeys[0] = getUKey();
        movementKeys[1] = getRKey();
        movementKeys[2] = getLKey();
        movementKeys[3] = getDKey();
    }

    @Override
    public void move(boolean [] keysPressed, int [] keysReleasedTime, Stage stage){
        keysPressed[getRKey()] = false; keysPressed[getLKey()] = false;
        keysPressed[getFastKey()] = true;
        if (true){
            Platform curPlat = nearestPlat(stage.getPlats());
            int midX = ((int)curPlat.getX() + (curPlat.getWidth()/2));
            
            if (Util.randint(1, 100) < 5) keysPressed[getUKey()] = Util.randBoolean();
            if (getY() > curPlat.getY()) keysPressed[getUKey()] = true;
 
            // System.out.println((int)curPlat.getX() + " " + (curPlat.getWidth()/2));
            if (!(midX - 50 <= getX() && getX() <= midX+1)){
                if (getX() < midX){
                    //System.out.println("LEFT");
                    keysPressed[getRKey()] = true;
                }
                else if (getX() > midX){
                    //System.out.println("RIGHT");
                    keysPressed[getLKey()] = true;
                }
            }
        }
        super.move(keysPressed, keysReleasedTime, stage);
    }
}
