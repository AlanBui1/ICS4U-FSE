package GameObjects.ThingsThatMove;

import java.util.HashMap;
import java.util.ArrayList;
import Utility.Util;
import GameObjects.ThingsThatMove.AttackStuff.*;

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
    public void move(boolean [] keysPressed, int [] keysReleasedTime, ArrayList <Platform> plats){
        keysPressed[getUKey()] = Util.randBoolean();
        keysPressed[getRKey()] = false; keysPressed[getLKey()] = false;
        if (getOnGround()){
            Platform curPlat = platOn(plats);
            int midX = ((int)curPlat.getX() + (curPlat.getWidth()/2));
            
            if (getX() < midX){
                keysPressed[getRKey()] = true;
            }
            else if (getX() > midX){
                keysPressed[getLKey()] = true;
            }
        }
        super.move(keysPressed, keysReleasedTime, plats);
    }
}
